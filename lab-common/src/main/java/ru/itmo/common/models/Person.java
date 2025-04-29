package ru.itmo.common.models;

import java.time.LocalDateTime;

public class Person implements Validatable {
    private LocalDateTime birthday; // Поле может быть null
    private Integer height; // Поле не может быть null, Значение поля должно быть больше 0
    private float weight; // Значение поля должно быть больше 0
    private String passportID; // Строка не может быть пустой, Поле может быть null

    public Person() {
    }

    public Person(LocalDateTime birthday, Integer height, float weight, String passportID) {
        this.birthday = birthday;
        this.height = height;
        this.weight = weight;
        this.passportID = passportID;
    }

    public LocalDateTime getBirthday() {
        return this.birthday;
    }

    public void setBirthday(LocalDateTime birthday) {
        this.birthday = birthday;
    }

    public Integer getHeight() {
        return this.height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public float getWeight() {
        return this.weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getPassportID() {
        return this.passportID;
    }

    public void setPassportID(String passportID) {
        this.passportID = passportID;
    }

    @Override
    public String toString() {
        return "Person{" +
                "birthday=" + birthday +
                ", height=" + height +
                ", weight=" + weight +
                ", passportID='" + passportID + '\'' +
                '}';
    }

    /**
     * Validates Person
     * @return returns true if all fields in Person are valid
     */
    @Override
    public boolean validate() {
        if (birthday != null && birthday.isAfter(LocalDateTime.now())) return false;
        if (height == null || height <= 0) return false;
        if (weight <= 0) return false;
        return passportID == null || !passportID.isEmpty();
    }
}
