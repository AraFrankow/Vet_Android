package com.example.acm4ap_arabela_frankow.Model;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Pet {
    private String name, tipoMascota, age, weight, genre, race, dateAntiparasitario, dateAntipulgas;

    // Dog vaccines
    private boolean vacuna_rabia;
    private String vacuna_rabia_revacuna;
    private boolean vacuna_parvovirus;
    private String vacuna_parvovirus_revacuna;
    private boolean vacuna_moquillo;
    private String vacuna_moquillo_revacuna;
    private boolean vacuna_hepatitis;
    private String vacuna_hepatitis_revacuna;
    private boolean vacuna_leptospirosis;
    private String vacuna_leptospirosis_revacuna;

    // Cat vaccines
    private boolean vacuna_trivalente;
    private String vacuna_trivalente_revacuna;
    private boolean vacuna_leucemia;
    private String vacuna_leucemia_revacuna;
    private boolean vacuna_rabia_gato;
    private String vacuna_rabia_gato_revacuna;

    public Pet() {}

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTipoMascota() {
        return tipoMascota;
    }

    public void setTipoMascota(String tipoMascota) {
        this.tipoMascota = tipoMascota;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
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

    public boolean isVacuna_rabia() {
        return vacuna_rabia;
    }

    public void setVacuna_rabia(boolean vacuna_rabia) {
        this.vacuna_rabia = vacuna_rabia;
    }

    public String getVacuna_rabia_revacuna() {
        return vacuna_rabia_revacuna;
    }

    public void setVacuna_rabia_revacuna(String vacuna_rabia_revacuna) {
        this.vacuna_rabia_revacuna = vacuna_rabia_revacuna;
    }

    public boolean isVacuna_parvovirus() {
        return vacuna_parvovirus;
    }

    public void setVacuna_parvovirus(boolean vacuna_parvovirus) {
        this.vacuna_parvovirus = vacuna_parvovirus;
    }

    public String getVacuna_parvovirus_revacuna() {
        return vacuna_parvovirus_revacuna;
    }

    public void setVacuna_parvovirus_revacuna(String vacuna_parvovirus_revacuna) {
        this.vacuna_parvovirus_revacuna = vacuna_parvovirus_revacuna;
    }

    public boolean isVacuna_moquillo() {
        return vacuna_moquillo;
    }

    public void setVacuna_moquillo(boolean vacuna_moquillo) {
        this.vacuna_moquillo = vacuna_moquillo;
    }

    public String getVacuna_moquillo_revacuna() {
        return vacuna_moquillo_revacuna;
    }

    public void setVacuna_moquillo_revacuna(String vacuna_moquillo_revacuna) {
        this.vacuna_moquillo_revacuna = vacuna_moquillo_revacuna;
    }

    public boolean isVacuna_hepatitis() {
        return vacuna_hepatitis;
    }

    public void setVacuna_hepatitis(boolean vacuna_hepatitis) {
        this.vacuna_hepatitis = vacuna_hepatitis;
    }

    public String getVacuna_hepatitis_revacuna() {
        return vacuna_hepatitis_revacuna;
    }

    public void setVacuna_hepatitis_revacuna(String vacuna_hepatitis_revacuna) {
        this.vacuna_hepatitis_revacuna = vacuna_hepatitis_revacuna;
    }

    public boolean isVacuna_leptospirosis() {
        return vacuna_leptospirosis;
    }

    public void setVacuna_leptospirosis(boolean vacuna_leptospirosis) {
        this.vacuna_leptospirosis = vacuna_leptospirosis;
    }

    public String getVacuna_leptospirosis_revacuna() {
        return vacuna_leptospirosis_revacuna;
    }

    public void setVacuna_leptospirosis_revacuna(String vacuna_leptospirosis_revacuna) {
        this.vacuna_leptospirosis_revacuna = vacuna_leptospirosis_revacuna;
    }

    public boolean isVacuna_trivalente() {
        return vacuna_trivalente;
    }

    public void setVacuna_trivalente(boolean vacuna_trivalente) {
        this.vacuna_trivalente = vacuna_trivalente;
    }

    public String getVacuna_trivalente_revacuna() {
        return vacuna_trivalente_revacuna;
    }

    public void setVacuna_trivalente_revacuna(String vacuna_trivalente_revacuna) {
        this.vacuna_trivalente_revacuna = vacuna_trivalente_revacuna;
    }

    public boolean isVacuna_leucemia() {
        return vacuna_leucemia;
    }

    public void setVacuna_leucemia(boolean vacuna_leucemia) {
        this.vacuna_leucemia = vacuna_leucemia;
    }

    public String getVacuna_leucemia_revacuna() {
        return vacuna_leucemia_revacuna;
    }

    public void setVacuna_leucemia_revacuna(String vacuna_leucemia_revacuna) {
        this.vacuna_leucemia_revacuna = vacuna_leucemia_revacuna;
    }

    public boolean isVacuna_rabia_gato() {
        return vacuna_rabia_gato;
    }

    public void setVacuna_rabia_gato(boolean vacuna_rabia_gato) {
        this.vacuna_rabia_gato = vacuna_rabia_gato;
    }

    public String getVacuna_rabia_gato_revacuna() {
        return vacuna_rabia_gato_revacuna;
    }

    public void setVacuna_rabia_gato_revacuna(String vacuna_rabia_gato_revacuna) {
        this.vacuna_rabia_gato_revacuna = vacuna_rabia_gato_revacuna;
    }
}
