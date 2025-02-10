package com.nounou.times.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthResponse {
    // Getters and Setters
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private UserInfo user;

    public AuthResponse(String accessToken, String refreshToken, long expiresIn, UserInfo user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public static class UserInfo {
        private Long id;
        private String email;
        private String nom;
        private String prenom;
        private String civilite;
        private String typeUtilisateur;
        private String photoUrl;

        public UserInfo(Long id, String email, String nom, String prenom, 
                       String civilite, String typeUtilisateur, String photoUrl) {
            this.id = id;
            this.email = email;
            this.nom = nom;
            this.prenom = prenom;
            this.civilite = civilite;
            this.typeUtilisateur = typeUtilisateur;
            this.photoUrl = photoUrl;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getPrenom() {
            return prenom;
        }

        public void setPrenom(String prenom) {
            this.prenom = prenom;
        }

        public String getCivilite() {
            return civilite;
        }

        public void setCivilite(String civilite) {
            this.civilite = civilite;
        }

        public String getTypeUtilisateur() {
            return typeUtilisateur;
        }

        public void setTypeUtilisateur(String typeUtilisateur) {
            this.typeUtilisateur = typeUtilisateur;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }
    }
}
