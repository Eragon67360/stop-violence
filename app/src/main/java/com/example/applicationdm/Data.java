package com.example.applicationdm;
/*----------------------------------------
*
*Classe Java : enregistre les coordonnées des utilisateurs
*
---------------------------------------- */
public class Data {
    static String Nom;
    static String Prenom;
    static String Age;
    static String Email;
    static String MotDePasse;
    static String Telephone;

    public Data(String nom, String prenom, String age, String email, String motDePasse, String telephone) {
        this.Nom = nom;
        this.Prenom = prenom;
        this.Age = age;
        this.Email = email;
        this.MotDePasse = motDePasse;
        this.Telephone = telephone;

    }


    public void setNom(String nom) {
        this.Nom = nom;
    }

    public void setPrenom(String prenom) {
        this.Prenom = prenom;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public void setAge(String age) {
        this.Age = age;
    }

    public void setMotDePasse(String motDePasse) {
        this.MotDePasse = motDePasse;
    }

    public void setTelephone(String telephone) {
        this.Telephone = telephone;
    }

    public String getNom() {
        return Nom;
    }

    public String getPrenom() {
        return Prenom;
    }

    public String getAge() {
        return Age;
    }

    public String getEmail() {
        return Email;
    }

    public String getMotDePasse() {
        return MotDePasse;
    }

    public String getTelephone() {
        return Telephone;
    }
}
