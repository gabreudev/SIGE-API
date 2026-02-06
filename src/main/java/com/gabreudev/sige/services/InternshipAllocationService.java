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


            // ETAPA 3: ESTÁGIO (210h)
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

            // Salvar todos os turnos
            shiftRepository.saveAll(studentShifts);
            totalShiftsCreated += studentShifts.size();

            log.info("Total de turnos criados para {}: {}", student.getName(), studentShifts.size());
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

        log.info("=== INÍCIO ALOCAÇÃO CIRCUITO ===");
        log.info("Data de início fornecida: {} ({})", startDate, startDate.getDayOfWeek());

        // Se a data inicial não for um dia útil, avançar para o próximo dia útil
        while (!isWorkday(currentDate)) {
            log.info("Data {} ({}) não é dia útil, avançando...", currentDate, currentDate.getDayOfWeek());
            currentDate = currentDate.plusDays(1);
        }

        log.info("Primeira data útil para circuito: {} ({})", currentDate, currentDate.getDayOfWeek());

        // 5 dias úteis × 6 horas = 30 horas
        int daysAllocated = 0;
        while (daysAllocated < 5) {
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
            shift.setHours(6);
            shift.setPeriod(ShiftPeriod.AFTERNOON);
            shift.setDate(currentDate);
            shift.setValidated(false);
            shifts.add(shift);

            daysAllocated++;
            
            // Avançar para o próximo dia
            currentDate = currentDate.plusDays(1);
            log.info("Avançando para próximo dia: {} ({})", currentDate, currentDate.getDayOfWeek());
        }

        log.info("=== FIM ALOCAÇÃO CIRCUITO ===");
        log.info("Total de {} dias úteis alocados para circuito", daysAllocated);
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
        int targetHours = 210;

        log.info("=== INÍCIO ALOCAÇÃO ESTÁGIO ===");
        log.info("Data de início fornecida: {} ({})", startDate, startDate.getDayOfWeek());

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
                    // Tentar manhã
                    if (Boolean.TRUE.equals(dayAvailability.get("morning"))) {
                        if (!hasConflict(student, currentDate, ShiftPeriod.MORNING, existingShifts, shifts)) {
                            Shift shift = createShift(student, unity, currentDate, ShiftPeriod.MORNING, ShiftType.INTERNSHIP, 6);
                            shifts.add(shift);
                            totalHours += 6;
                            log.info("Estágio alocado - manhã: {} ({}) - Total: {}h", currentDate, currentDate.getDayOfWeek(), totalHours);
                            if (totalHours >= targetHours) break;
                        }
                    }

                    // Tentar tarde
                    if (Boolean.TRUE.equals(dayAvailability.get("afternoon"))) {
                        if (!hasConflict(student, currentDate, ShiftPeriod.AFTERNOON, existingShifts, shifts)) {
                            Shift shift = createShift(student, unity, currentDate, ShiftPeriod.AFTERNOON, ShiftType.INTERNSHIP, 6);
                            shifts.add(shift);
                            totalHours += 6;
                            log.info("Estágio alocado - tarde: {} ({}) - Total: {}h", currentDate, currentDate.getDayOfWeek(), totalHours);
                            if (totalHours >= targetHours) break;
                        }
                    }
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        if (totalHours < targetHours) {
            throw new RuntimeException("Não foi possível alocar as 210 horas de estágio para o estudante: " + student.getName());
        }

        return shifts;
    }

    private List<Shift> allocateReports(User student, Unity unity, List<Shift> internshipShifts, List<Shift> allExistingShifts) {
        List<Shift> reportShifts = new ArrayList<>();
        int totalHours = 0;
        int targetHours = 30; // 15 turnos × 2h

        // Agrupar turnos de estágio por semana
        Map<Integer, List<Shift>> shiftsByWeek = internshipShifts.stream()
                .collect(Collectors.groupingBy(shift -> getWeekNumber(shift.getDate())));

        for (Map.Entry<Integer, List<Shift>> entry : shiftsByWeek.entrySet()) {
            if (totalHours >= targetHours) break;

            List<Shift> weekShifts = entry.getValue();
            // Pegar o primeiro dia da semana
            LocalDate reportDate = weekShifts.get(0).getDate();

            // Tentar alocar relatório em período oposto ao estágio
            ShiftPeriod internshipPeriod = weekShifts.get(0).getPeriod();
            ShiftPeriod reportPeriod = internshipPeriod == ShiftPeriod.MORNING ? ShiftPeriod.AFTERNOON : ShiftPeriod.MORNING;

            if (!hasConflict(student, reportDate, reportPeriod, allExistingShifts, reportShifts)) {
                Shift reportShift = createShift(student, unity, reportDate, reportPeriod, ShiftType.REPORT, 2);
                reportShifts.add(reportShift);
                totalHours += 2;
            }
        }

        return reportShifts;
    }

    private List<Shift> allocateCaseStudy(User student, Unity unity, LocalDate startDate, LocalDate endDate, List<Shift> existingShifts) {
        List<Shift> shifts = new ArrayList<>();
        LocalDate currentDate = startDate;

        log.info("=== INÍCIO ALOCAÇÃO ESTUDO DE CASO ===");
        log.info("Data de início fornecida: {} ({})", startDate, startDate.getDayOfWeek());

        // Se a data inicial não for um dia útil, avançar para o próximo dia útil
        while (!isWorkday(currentDate)) {
            log.info("Data {} ({}) não é dia útil, avançando...", currentDate, currentDate.getDayOfWeek());
            currentDate = currentDate.plusDays(1);
        }

        log.info("Primeira data útil para estudo de caso: {} ({})", currentDate, currentDate.getDayOfWeek());

        int totalHours = 0;
        int targetHours = 30; // 5 dias × 6h

        while (totalHours < targetHours && !currentDate.isAfter(endDate)) {
            // Pular finais de semana
            if (!isWorkday(currentDate)) {
                log.info("Data {} não é dia útil, pulando...", currentDate);
                currentDate = currentDate.plusDays(1);
                continue;
            }

            if (!currentDate.isAfter(endDate)) {
                // Tentar manhã primeiro
                if (!hasConflict(student, currentDate, ShiftPeriod.MORNING, existingShifts, shifts)) {
                    Shift shift = createShift(student, unity, currentDate, ShiftPeriod.MORNING, ShiftType.CASE_STUDY, 6);
                    shifts.add(shift);
                    totalHours += 6;
                    log.info("Estudo de caso alocado - manhã: {} ({}) - Total: {}h", currentDate, currentDate.getDayOfWeek(), totalHours);
                } else if (!hasConflict(student, currentDate, ShiftPeriod.AFTERNOON, existingShifts, shifts)) {
                    // Se manhã tiver conflito, tentar tarde
                    Shift shift = createShift(student, unity, currentDate, ShiftPeriod.AFTERNOON, ShiftType.CASE_STUDY, 6);
                    shifts.add(shift);
                    totalHours += 6;
                    log.info("Estudo de caso alocado - tarde: {} ({}) - Total: {}h", currentDate, currentDate.getDayOfWeek(), totalHours);
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        // Se não conseguir dias suficientes antes do fim, permitir alocar em dias já usados em turno oposto
        if (totalHours < targetHours) {
            currentDate = startDate;
            // Garantir que começa em dia útil
            while (!isWorkday(currentDate)) {
                currentDate = currentDate.plusDays(1);
            }

            while (totalHours < targetHours && !currentDate.isAfter(endDate)) {
                // Garantir que é dia útil
                while (!isWorkday(currentDate) && !currentDate.isAfter(endDate)) {
                    currentDate = currentDate.plusDays(1);
                }

                if (!currentDate.isAfter(endDate)) {
                    // Criar variável final para uso na lambda
                    final LocalDate dateToCheck = currentDate;

                    // Verificar se já existe turno nesse dia
                    List<Shift> shiftsOnDate = existingShifts.stream()
                            .filter(s -> s.getDate().equals(dateToCheck))
                            .collect(Collectors.toList());

                    if (!shiftsOnDate.isEmpty()) {
                        // Pegar período oposto
                        ShiftPeriod existingPeriod = shiftsOnDate.get(0).getPeriod();
                        ShiftPeriod oppositePeriod = existingPeriod == ShiftPeriod.MORNING ? ShiftPeriod.AFTERNOON : ShiftPeriod.MORNING;

                        if (!hasConflict(student, currentDate, oppositePeriod, existingShifts, shifts)) {
                            Shift shift = createShift(student, unity, currentDate, oppositePeriod, ShiftType.CASE_STUDY, 6);
                            shifts.add(shift);
                            totalHours += 6;
                        }
                    }
                }
                currentDate = currentDate.plusDays(1);
            }
        }

        if (totalHours < targetHours) {
            throw new RuntimeException("Não foi possível alocar as 30 horas de estudo de caso para o estudante: " + student.getName());
        }

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
        shift.setValidated(false);
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
