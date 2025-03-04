package com.nounou.times.dto;

public class SignupRequest {
    private String email;
    private String password;
    private String nom;
    private String prenom;
    private String telephone;
    private String adresse;
    private String pageEmploiId;

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getPageEmploiId() { return pageEmploiId; }
    public void setPageEmploiId(String pageEmploiId) { this.pageEmploiId = pageEmploiId; }
}
