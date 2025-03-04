package com.nounou.times.dto;

import java.time.LocalDateTime;

public class ImprevisRequest {
    private String description;
    private LocalDateTime dateHeure;
    private String type;
    private Long enfantId;

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getEnfantId() { return enfantId; }
    public void setEnfantId(Long enfantId) { this.enfantId = enfantId; }
}
