package com.example.acm4ap_arabela_frankow.Model;

public class Pet {
    String name, age, color;
    public Pet(){}

    public Pet(String name, String age, String color){
        this.name = name;
        this.age = age;
        this.color = color;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
