package com.example.applicationdm;

public class NewContact {
    private String nom;
    private String telephone;

    public NewContact(String nom, String telephone) {
        this.nom = nom;
        this.telephone = telephone;
    }
    public NewContact() {

    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
