package com.gabreudev.sige.entities.report;

public enum ReportStatus {
    SUBMITTED,      // Submetido - aguardando revisão
    APPROVED,       // Aprovado pelo preceptor/supervisor
    REJECTED        // Rejeitado - aluno pode editar e preceptor pode revisar novamente
}
