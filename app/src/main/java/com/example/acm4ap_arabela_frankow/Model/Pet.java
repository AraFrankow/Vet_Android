package com.example.acm4ap_arabela_frankow.Model;

public class Pet {
    String name, age, weight, genre, nameVacuna, dateVacuna, dateAntiparasitario, dateAntipulgas;
    public Pet(){}

    public Pet(String name, String age, String genre, String nameVacuna, String dateVacuna, String dateAntiparasitario, String dateAntipulgas){
        this.name = name;
        this.age = age;
        this.genre = genre;
        this.nameVacuna = nameVacuna;
        this.dateVacuna = dateVacuna;
        this.dateAntiparasitario = dateAntiparasitario;
        this.dateAntipulgas = dateAntipulgas;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
    public String getNameVacuna() {
        return nameVacuna;
    }

    public void setNameVacuna(String nameVacuna) {
        this.nameVacuna = nameVacuna;
    }

    public String getDateVacuna() {
        return dateVacuna;
    }

    public void setDateVacuna(String dateVacuna) {
        this.dateVacuna = dateVacuna;
    }
    public String getDateAntiparasitario() {
        return dateAntiparasitario;
    }

    public void setDateAntiparasitario(String dateAntiparasitario) {
        this.dateAntiparasitario = dateAntiparasitario;
    }
    public String getDateAntipulgas() {
        return dateAntipulgas;
    }

    public void setDateAntipulgas(String dateAntipulgas) {
        this.dateAntipulgas = dateAntipulgas;
    }
}
