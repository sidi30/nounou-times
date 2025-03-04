package com.nounou.times.dto;

import java.time.LocalDateTime;

public class ReprogrammationRequest {
    private LocalDateTime nouvelleDate;
    private String raison;

    public LocalDateTime getNouvelleDate() { return nouvelleDate; }
    public void setNouvelleDate(LocalDateTime nouvelleDate) { this.nouvelleDate = nouvelleDate; }

    public String getRaison() { return raison; }
    public void setRaison(String raison) { this.raison = raison; }
}
