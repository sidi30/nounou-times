package com.nounou.times.dto;

import java.time.LocalDate;

public class InvitationRequest {
    private String emailParent;
    private String nomEnfant;
    private String prenomEnfant;
    private LocalDate dateNaissance;

    public String getEmailParent() { return emailParent; }
    public void setEmailParent(String emailParent) { this.emailParent = emailParent; }

    public String getNomEnfant() { return nomEnfant; }
    public void setNomEnfant(String nomEnfant) { this.nomEnfant = nomEnfant; }

    public String getPrenomEnfant() { return prenomEnfant; }
    public void setPrenomEnfant(String prenomEnfant) { this.prenomEnfant = prenomEnfant; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
}
