package com.nounou.times.dto;

import java.time.LocalDate;

public class AbsenceRequest {
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String motif;
    private Long enfantId;

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public Long getEnfantId() { return enfantId; }
    public void setEnfantId(Long enfantId) { this.enfantId = enfantId; }
}
