package com.gabreudev.sige.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gabreudev.sige.entities.shift.Shift;
import com.gabreudev.sige.entities.shift.ShiftPeriod;
import com.gabreudev.sige.entities.shift.ShiftType;
import com.gabreudev.sige.entities.shift.dto.InternshipAllocationRequestDTO;
import com.gabreudev.sige.entities.shift.dto.InternshipAllocationResponseDTO;
import com.gabreudev.sige.entities.unity.Unity;
import com.gabreudev.sige.entities.user.User;
import com.gabreudev.sige.repositories.ShiftRepository;
import com.gabreudev.sige.repositories.UnityRepository;
import com.gabreudev.sige.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InternshipAllocationService {

    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository;
    private final UnityRepository unityRepository;

    @Transactional
    public InternshipAllocationResponseDTO allocateInternship1(InternshipAllocationRequestDTO request) {
        // Validar entrada
        if (request.studentIds() == null || request.studentIds().isEmpty()) {
            throw new RuntimeException("Lista de estudantes não pode estar vazia");
        }
        if (request.unityIds() == null || request.unityIds().isEmpty()) {
            throw new RuntimeException("Lista de unidades não pode estar vazia");
        }
        if (request.startDate() == null || request.endDate() == null) {
            throw new RuntimeException("Datas de início e fim são obrigatórias");
        }
        if (request.startDate().isAfter(request.endDate())) {
            throw new RuntimeException("Data de início deve ser anterior à data de fim");
        }

        List<User> students = userRepository.findAllById(request.studentIds());
        if (students.size() != request.studentIds().size()) {
            throw new RuntimeException("Alguns estudantes não foram encontrados");
        }

        List<Unity> availableUnities = unityRepository.findAllById(request.unityIds());
        if (availableUnities.isEmpty()) {
            throw new RuntimeException("Nenhuma unidade válida foi encontrada");
        }

        int totalShiftsCreated = 0;

        for (User student : students) {
            log.info("Alocando estágios para estudante: {}", student.getName());

            List<Shift> studentShifts = new ArrayList<>();

            // ETAPA 1: CIRCUITO (30h = 5 dias × 6h) - sem unidade específica
            LocalDate circuitStartDate = request.startDate();
            List<Shift> circuitShifts = allocateCircuit(student, circuitStartDate);
            studentShifts.addAll(circuitShifts);
            log.info("Circuito alocado: {} turnos", circuitShifts.size());

            // Próxima data disponível após circuito
            LocalDate afterCircuit = getNextWorkday(circuitShifts.get(circuitShifts.size() - 1).getDate());

            // ETAPA 2: ESCOLHER UNIDADE
            Unity selectedUnity = selectUnityForStudent(student, availableUnities, request.unityIds());
            log.info("Unidade selecionada: {}", selectedUnity.getName());


            // ETAPA 3: ESTÁGIO (300h = 75 turnos × 4h)
            List<Shift> internshipShifts = allocateInternship(
                    student,
                    selectedUnity,
                    afterCircuit,
                    request.endDate(),
                    studentShifts
            );
            studentShifts.addAll(internshipShifts);
            log.info("Estágio alocado: {} turnos", internshipShifts.size());

            // ETAPA 4: RELATÓRIO (30h = 15 turnos × 2h)
            List<Shift> reportShifts = allocateReports(
                    student,
                    null, // Relatório não requer unidade específica
                    internshipShifts,
                    studentShifts
            );
            studentShifts.addAll(reportShifts);
            log.info("Relatórios alocados: {} turnos", reportShifts.size());

            // ETAPA 5: ESTUDO DE CASO (30h = 5 dias × 6h)
            LocalDate caseStudyStart = internshipShifts.get(internshipShifts.size() - 1).getDate();
            caseStudyStart = getNextWorkday(caseStudyStart);
            List<Shift> caseStudyShifts = allocateCaseStudy(
                    student,
                    null, // Estudo de caso não requer unidade específica
                    caseStudyStart,
                    request.endDate(),
                    studentShifts
            );
            studentShifts.addAll(caseStudyShifts);
            log.info("Estudo de caso alocado: {} turnos", caseStudyShifts.size());

            // Calcular total de horas alocadas
            int totalHoursAllocated = studentShifts.stream()
                    .mapToInt(Shift::getHours)
                    .sum();

            // Salvar todos os turnos
            shiftRepository.saveAll(studentShifts);
            totalShiftsCreated += studentShifts.size();

            log.info("========================================");
            log.info("RESUMO ALOCAÇÃO - {}", student.getName());
            log.info("Circuito: {} turnos (20h)", circuitShifts.size());
            log.info("Estágio: {} turnos (300h)", internshipShifts.size());
            log.info("Relatórios: {} turnos (30h)", reportShifts.size());
            log.info("Estudo de Caso: {} turnos (20h)", caseStudyShifts.size());
            log.info("TOTAL DE TURNOS: {}", studentShifts.size());
            log.info("TOTAL DE HORAS: {}h (esperado: 370h)", totalHoursAllocated);
            log.info("========================================");
        }

        return new InternshipAllocationResponseDTO(
                "Alocação de estágios realizada com sucesso!",
                students.size(),
                totalShiftsCreated
        );
    }

    private List<Shift> allocateCircuit(User student, LocalDate startDate) {
        List<Shift> shifts = new ArrayList<>();
        LocalDate currentDate = startDate;
        int hoursPerShift = 4; // 4 horas por turno
        int totalDays = 5; // 5 dias úteis
        int totalHours = totalDays * hoursPerShift; // 20 horas

        log.info("=== INÍCIO ALOCAÇÃO CIRCUITO ===");
        log.info("Data de início fornecida: {} ({})", startDate, startDate.getDayOfWeek());
        log.info("Meta: {} dias × {}h = {}h", totalDays, hoursPerShift, totalHours);

        // Se a data inicial não for um dia útil, avançar para o próximo dia útil
        while (!isWorkday(currentDate)) {
            log.info("Data {} ({}) não é dia útil, avançando...", currentDate, currentDate.getDayOfWeek());
            currentDate = currentDate.plusDays(1);
        }

        log.info("Primeira data útil para circuito: {} ({})", currentDate, currentDate.getDayOfWeek());

        // Alocar 5 dias úteis de circuito
        int daysAllocated = 0;
        while (daysAllocated < totalDays) {
            // Garantir que é dia útil
            if (!isWorkday(currentDate)) {
                log.info("Data {} ({}) não é dia útil, pulando para próximo dia...", currentDate, currentDate.getDayOfWeek());
                currentDate = currentDate.plusDays(1);
                continue;
            }

            log.info("Alocando circuito dia {}: {} ({})", (daysAllocated + 1), currentDate, currentDate.getDayOfWeek());

            Shift shift = new Shift();
            shift.setUser(student);
            shift.setUnity(null); // Circuito não requer unidade específica
            shift.setType(ShiftType.CIRCUIT);
            shift.setHours(hoursPerShift);
            shift.setPeriod(ShiftPeriod.AFTERNOON);
            shift.setDate(currentDate);
            shifts.add(shift);

            daysAllocated++;
            
            // Avançar para o próximo dia
            currentDate = currentDate.plusDays(1);
            log.info("Avançando para próximo dia: {} ({})", currentDate, currentDate.getDayOfWeek());
        }

        log.info("=== FIM ALOCAÇÃO CIRCUITO ===");
        log.info("Total de {} dias úteis alocados = {}h", daysAllocated, totalHours);
        return shifts;
    }

    private Unity selectUnityForStudent(User student, List<Unity> availableUnities, List<UUID> requestUnityIds) {
        // Obter todas as unidades do sistema
        List<Unity> allUnities = unityRepository.findAll();

        // Listas de candidatos
        List<Unity> preferredCandidates = new ArrayList<>();
        List<Unity> requestCandidates = new ArrayList<>();
        List<Unity> allCandidates = new ArrayList<>();

        // a) Coletar unidades preferidas do estudante
        if (student.getPreferredUnityIds() != null && !student.getPreferredUnityIds().isEmpty()) {
            for (UUID preferredId : student.getPreferredUnityIds()) {
                Unity unity = unityRepository.findById(preferredId).orElse(null);
                if (unity != null && hasAvailableSlots(unity)) {
                    preferredCandidates.add(unity);
                }
            }
        }

        // b) Coletar unidades da requisição
        for (UUID unityId : requestUnityIds) {
            Unity unity = unityRepository.findById(unityId).orElse(null);
            if (unity != null && hasAvailableSlots(unity)) {
                requestCandidates.add(unity);
            }
        }

        // c) Coletar qualquer unidade do sistema
        for (Unity unity : allUnities) {
            if (hasAvailableSlots(unity)) {
                allCandidates.add(unity);
            }
        }

        // Tentar em cada lista, priorizando por gênero
        Unity selected = selectUnityByGenderPriority(student, preferredCandidates);
        if (selected != null) return selected;

        selected = selectUnityByGenderPriority(student, requestCandidates);
        if (selected != null) return selected;

        selected = selectUnityByGenderPriority(student, allCandidates);
        if (selected != null) return selected;

        throw new RuntimeException("Todas as unidades estão com lotação máxima. Não foi possível alocar o estudante: " + student.getName());
    }

    private Unity selectUnityByGenderPriority(User student, List<Unity> candidates) {
        if (candidates.isEmpty()) return null;

        Boolean isMale = student.getMale();

        // Se o estudante for homem, priorizar unidades sem homens ou com apenas mulheres
        if (Boolean.TRUE.equals(isMale)) {
            // 1. Tentar unidades sem nenhum estudante alocado
            for (Unity unity : candidates) {
                if (getStudentsInUnity(unity).isEmpty()) {
                    return unity;
                }
            }

            // 2. Tentar unidades que só têm mulheres
            for (Unity unity : candidates) {
                List<User> studentsInUnity = getStudentsInUnity(unity);
                if (!studentsInUnity.isEmpty() && studentsInUnity.stream().allMatch(u -> Boolean.FALSE.equals(u.getMale()))) {
                    return unity;
                }
            }

            // 3. Última opção: aceitar unidade com homens (evitar ao máximo)
            for (Unity unity : candidates) {
                return unity;
            }
        } else {
            // Se o estudante for mulher, qualquer unidade disponível serve
            // Mas ainda assim preferir unidades sem homens ou com menos estudantes

            // 1. Tentar unidades vazias
            for (Unity unity : candidates) {
                if (getStudentsInUnity(unity).isEmpty()) {
                    return unity;
                }
            }

            // 2. Tentar unidades só com mulheres
            for (Unity unity : candidates) {
                List<User> studentsInUnity = getStudentsInUnity(unity);
                if (!studentsInUnity.isEmpty() && studentsInUnity.stream().allMatch(u -> Boolean.FALSE.equals(u.getMale()))) {
                    return unity;
                }
            }

            // 3. Aceitar qualquer unidade disponível
            for (Unity unity : candidates) {
                return unity;
            }
        }

        return null;
    }

    private List<User> getStudentsInUnity(Unity unity) {
        // Buscar todos os turnos da unidade
        List<Shift> unityShifts = shiftRepository.findByUnity_Id(unity.getId());

        // Obter IDs distintos de estudantes
        List<UUID> distinctStudentIds = unityShifts.stream()
                .map(shift -> shift.getUser().getId())
                .distinct()
                .collect(Collectors.toList());

        // Buscar os usuários
        return userRepository.findAllById(distinctStudentIds);
    }

    private boolean hasAvailableSlots(Unity unity) {
        // Buscar todos os turnos da unidade
        List<Shift> unityShifts = shiftRepository.findByUnity_Id(unity.getId());

        // Contar estudantes distintos
        long distinctStudents = unityShifts.stream()
                .map(shift -> shift.getUser().getId())
                .distinct()
                .count();

        // Comparar com maxStudentsPerDay
        Integer maxStudents = unity.getMaxStudentsPerDay();
        if (maxStudents == null) {
            maxStudents = 2; // valor padrão
        }

        return distinctStudents < maxStudents;
    }

    private List<Shift> allocateInternship(User student, Unity unity, LocalDate startDate, LocalDate endDate, List<Shift> existingShifts) {
        List<Shift> shifts = new ArrayList<>();
        LocalDate currentDate = startDate;
        int totalHours = 0;
        int targetHours = 300; // 300 horas de estágio
        int hoursPerShift = 4; // 4 horas por turno
        int targetShifts = targetHours / hoursPerShift; // 75 turnos
        int shiftsPerWeek = 5; // 5 turnos por semana (1 por dia útil)
        int targetWeeks = targetShifts / shiftsPerWeek; // 15 semanas

        log.info("=== INÍCIO ALOCAÇÃO ESTÁGIO ===");
        log.info("Data de início fornecida: {} ({})", startDate, startDate.getDayOfWeek());
        log.info("Meta: {}h = {} turnos de {}h (~{} semanas, {} turnos/semana)", 
                targetHours, targetShifts, hoursPerShift, targetWeeks, shiftsPerWeek);

        Map<String, Object> availability = unity.getAvailability();
        if (availability == null || availability.isEmpty()) {
            throw new RuntimeException("Unidade " + unity.getName() + " não possui disponibilidade configurada");
        }

        // Se a data inicial não for um dia útil, avançar para o próximo dia útil
        while (!isWorkday(currentDate)) {
            log.info("Data {} ({}) não é dia útil, avançando...", currentDate, currentDate.getDayOfWeek());
            currentDate = currentDate.plusDays(1);
        }

        log.info("Primeira data útil para estágio: {} ({})", currentDate, currentDate.getDayOfWeek());

        while (totalHours < targetHours && !currentDate.isAfter(endDate)) {
            if (isWorkday(currentDate)) {
                String dayOfWeek = getDayOfWeekKey(currentDate);
                Map<String, Boolean> dayAvailability = (Map<String, Boolean>) availability.get(dayOfWeek);

                if (dayAvailability != null) {
                    boolean morningAvailable = Boolean.TRUE.equals(dayAvailability.get("morning"));
                    boolean afternoonAvailable = Boolean.TRUE.equals(dayAvailability.get("afternoon"));

                    // REGRA: Alocar 1 turno de 4h por dia conforme disponibilidade da unidade
                    // - Se apenas manhã disponível: aloca manhã
                    // - Se apenas tarde disponível: aloca tarde
                    // - Se ambos disponíveis: aloca manhã (ou alterna)
                    // - Se nenhum disponível: pula o dia
                    
                    boolean allocated = false;

                    // Verificar disponibilidade de manhã
                    if (morningAvailable && !allocated) {
                        if (!hasConflict(student, currentDate, ShiftPeriod.MORNING, existingShifts, shifts)) {
                            Shift shift = createShift(student, unity, currentDate, ShiftPeriod.MORNING, ShiftType.INTERNSHIP, hoursPerShift);
                            shifts.add(shift);
                            totalHours += hoursPerShift;
                            allocated = true;
                            log.info("Estágio alocado - {} disponível: MANHÃ em {} ({}) - Total: {}h / {}h", 
                                    (morningAvailable && afternoonAvailable) ? "Manhã e Tarde" : "Manhã", 
                                    currentDate, currentDate.getDayOfWeek(), totalHours, targetHours);
                        }
                    }

                    // Verificar disponibilidade de tarde (se manhã não foi alocada)
                    if (afternoonAvailable && !allocated) {
                        if (!hasConflict(student, currentDate, ShiftPeriod.AFTERNOON, existingShifts, shifts)) {
                            Shift shift = createShift(student, unity, currentDate, ShiftPeriod.AFTERNOON, ShiftType.INTERNSHIP, hoursPerShift);
                            shifts.add(shift);
                            totalHours += hoursPerShift;
                            allocated = true;
                            log.info("Estágio alocado - Tarde disponível: TARDE em {} ({}) - Total: {}h / {}h", 
                                    currentDate, currentDate.getDayOfWeek(), totalHours, targetHours);
                        }
                    }

                    // Log se o dia não teve disponibilidade ou teve conflito
                    if (!allocated && (morningAvailable || afternoonAvailable)) {
                        log.debug("Não foi possível alocar em {} - Conflito ou já alocado", currentDate);
                    }

                    if (totalHours >= targetHours) break;
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        if (totalHours < targetHours) {
            throw new RuntimeException("Não foi possível alocar as " + targetHours + " horas de estágio para o estudante: " + student.getName());
        }

        int weeksUsed = (int) Math.ceil((double) shifts.size() / shiftsPerWeek);
        log.info("=== FIM ALOCAÇÃO ESTÁGIO ===");
        log.info("Total de {}h alocadas em {} turnos de {}h (~{} semanas)", totalHours, shifts.size(), hoursPerShift, weeksUsed);
        return shifts;
    }

    private List<Shift> allocateReports(User student, Unity unity, List<Shift> internshipShifts, List<Shift> allExistingShifts) {
        List<Shift> reportShifts = new ArrayList<>();
        int totalHours = 0;

        log.info("=== INÍCIO ALOCAÇÃO RELATÓRIOS ===");
        log.info("Alocando 1 relatório de 2h por semana de estágio");

        // Agrupar turnos de estágio por semana
        Map<Integer, List<Shift>> shiftsByWeek = internshipShifts.stream()
                .collect(Collectors.groupingBy(shift -> getWeekNumber(shift.getDate())));

        log.info("Total de semanas com estágio: {}", shiftsByWeek.size());

        // Alocar exatamente 1 relatório por semana (no primeiro dia da semana)
        for (Map.Entry<Integer, List<Shift>> entry : shiftsByWeek.entrySet()) {
            List<Shift> weekShifts = entry.getValue();
            // Ordenar turnos da semana por data
            weekShifts.sort((s1, s2) -> s1.getDate().compareTo(s2.getDate()));
            
            // Pegar o primeiro dia da semana com estágio
            LocalDate reportDate = weekShifts.get(0).getDate();

            // Alocar relatório em período oposto ao estágio
            ShiftPeriod internshipPeriod = weekShifts.get(0).getPeriod();
            ShiftPeriod reportPeriod = internshipPeriod == ShiftPeriod.MORNING ? ShiftPeriod.AFTERNOON : ShiftPeriod.MORNING;

            // Verificar conflito antes de alocar
            if (!hasConflict(student, reportDate, reportPeriod, allExistingShifts, reportShifts)) {
                Shift reportShift = createShift(student, unity, reportDate, reportPeriod, ShiftType.REPORT, 2);
                reportShifts.add(reportShift);
                totalHours += 2;
                log.info("Relatório semana {}: {} ({}) - período {}", entry.getKey(), reportDate, reportDate.getDayOfWeek(), reportPeriod);
            } else {
                // Se houver conflito no primeiro dia, tentar outros dias da mesma semana
                boolean allocated = false;
                for (Shift internshipShift : weekShifts) {
                    if (allocated) break;
                    
                    LocalDate altDate = internshipShift.getDate();
                    ShiftPeriod altPeriod = internshipShift.getPeriod() == ShiftPeriod.MORNING ? ShiftPeriod.AFTERNOON : ShiftPeriod.MORNING;
                    
                    if (!hasConflict(student, altDate, altPeriod, allExistingShifts, reportShifts)) {
                        Shift reportShift = createShift(student, unity, altDate, altPeriod, ShiftType.REPORT, 2);
                        reportShifts.add(reportShift);
                        totalHours += 2;
                        allocated = true;
                        log.info("Relatório semana {} (alternativo): {} ({}) - período {}", entry.getKey(), altDate, altDate.getDayOfWeek(), altPeriod);
                    }
                }
                
                if (!allocated) {
                    log.warn("Não foi possível alocar relatório na semana {}", entry.getKey());
                }
            }
        }

        log.info("=== FIM ALOCAÇÃO RELATÓRIOS ===");
        log.info("Total de relatórios alocados: {} ({}h de {} semanas)", reportShifts.size(), totalHours, shiftsByWeek.size());

        return reportShifts;
    }

    private List<Shift> allocateCaseStudy(User student, Unity unity, LocalDate startDate, LocalDate endDate, List<Shift> existingShifts) {
        List<Shift> shifts = new ArrayList<>();
        LocalDate currentDate = startDate;
        int hoursPerShift = 4; // 4 horas por turno
        int totalDays = 5; // 5 dias úteis
        int totalHours = totalDays * hoursPerShift; // 20 horas

        log.info("=== INÍCIO ALOCAÇÃO ESTUDO DE CASO ===");
        log.info("Data de início fornecida: {} ({})", startDate, startDate.getDayOfWeek());
        log.info("Meta: {} dias × {}h = {}h", totalDays, hoursPerShift, totalHours);

        // Se a data inicial não for um dia útil, avançar para o próximo dia útil
        while (!isWorkday(currentDate)) {
            log.info("Data {} ({}) não é dia útil, avançando...", currentDate, currentDate.getDayOfWeek());
            currentDate = currentDate.plusDays(1);
        }

        log.info("Primeira data útil para estudo de caso: {} ({})", currentDate, currentDate.getDayOfWeek());

        // Alocar 5 dias úteis de estudo de caso
        int daysAllocated = 0;
        while (daysAllocated < totalDays && !currentDate.isAfter(endDate)) {
            // Pular finais de semana
            if (!isWorkday(currentDate)) {
                log.info("Data {} não é dia útil, pulando...", currentDate);
                currentDate = currentDate.plusDays(1);
                continue;
            }

            // Tentar alocar 1 turno neste dia (preferência: manhã, alternativa: tarde)
            boolean allocated = false;
            
            // Tentar manhã primeiro
            if (!hasConflict(student, currentDate, ShiftPeriod.MORNING, existingShifts, shifts)) {
                Shift shift = createShift(student, unity, currentDate, ShiftPeriod.MORNING, ShiftType.CASE_STUDY, hoursPerShift);
                shifts.add(shift);
                daysAllocated++;
                allocated = true;
                log.info("Estudo de caso alocado dia {} - manhã: {} ({})", daysAllocated, currentDate, currentDate.getDayOfWeek());
            } else if (!hasConflict(student, currentDate, ShiftPeriod.AFTERNOON, existingShifts, shifts)) {
                // Se manhã tiver conflito, tentar tarde
                Shift shift = createShift(student, unity, currentDate, ShiftPeriod.AFTERNOON, ShiftType.CASE_STUDY, hoursPerShift);
                shifts.add(shift);
                daysAllocated++;
                allocated = true;
                log.info("Estudo de caso alocado dia {} - tarde: {} ({})", daysAllocated, currentDate, currentDate.getDayOfWeek());
            }

            currentDate = currentDate.plusDays(1);
        }

        if (daysAllocated < totalDays) {
            throw new RuntimeException("Não foi possível alocar os " + totalDays + " dias de estudo de caso para o estudante: " + student.getName());
        }

        log.info("=== FIM ALOCAÇÃO ESTUDO DE CASO ===");
        log.info("Total de {} dias úteis alocados = {}h", daysAllocated, totalHours);
        return shifts;
    }

    private Shift createShift(User student, Unity unity, LocalDate date, ShiftPeriod period, ShiftType type, int hours) {
        Shift shift = new Shift();
        shift.setUser(student);
        shift.setUnity(unity);
        shift.setDate(date);
        shift.setPeriod(period);
        shift.setType(type);
        shift.setHours(hours);
        return shift;
    }

    private boolean hasConflict(User student, LocalDate date, ShiftPeriod period, List<Shift> existingShifts, List<Shift> newShifts) {
        // Verificar conflito em turnos existentes
        boolean existingConflict = existingShifts.stream()
                .anyMatch(s -> s.getUser().getId().equals(student.getId())
                        && s.getDate().equals(date)
                        && s.getPeriod() == period);

        // Verificar conflito em novos turnos
        boolean newConflict = newShifts.stream()
                .anyMatch(s -> s.getUser().getId().equals(student.getId())
                        && s.getDate().equals(date)
                        && s.getPeriod() == period);

        return existingConflict || newConflict;
    }

    private boolean isWorkday(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }

    private LocalDate getNextWorkday(LocalDate date) {
        LocalDate nextDay = date.plusDays(1);
        while (!isWorkday(nextDay)) {
            nextDay = nextDay.plusDays(1);
        }
        return nextDay;
    }

    private String getDayOfWeekKey(LocalDate date) {
        return switch (date.getDayOfWeek()) {
            case MONDAY -> "monday";
            case TUESDAY -> "tuesday";
            case WEDNESDAY -> "wednesday";
            case THURSDAY -> "thursday";
            case FRIDAY -> "friday";
            case SATURDAY -> "saturday";
            case SUNDAY -> "sunday";
        };
    }

    private int getWeekNumber(LocalDate date) {
        return date.getDayOfYear() / 7;
    }
}
