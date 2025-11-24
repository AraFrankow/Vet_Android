package com.example.acm4ap_arabela_frankow.Model;

public class Pet {
    String name, age, genre;
    public Pet(){}

    public Pet(String name, String age, String genre){
        this.name = name;
        this.age = age;
        this.genre = genre;
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
}
