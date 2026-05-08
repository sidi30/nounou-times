package com.nounou.times.dto;

import com.nounou.times.model.Nounou;

import java.time.LocalDate;

public class NounouDto {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private LocalDate dateDebut;
    private String pageEmploiId;

    public NounouDto(Nounou nounou) {
        this.id = nounou.id;
        this.nom = nounou.getNom();
        this.prenom = nounou.getPrenom();
        this.email = nounou.getEmail();
        this.telephone = nounou.getTelephone();
        this.adresse = nounou.getAdresse();
        this.dateDebut = nounou.getDateDebut();
        this.pageEmploiId = nounou.getPageEmploiId();
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getTelephone() { return telephone; }
    public String getAdresse() { return adresse; }
    public LocalDate getDateDebut() { return dateDebut; }
    public String getPageEmploiId() { return pageEmploiId; }
}
