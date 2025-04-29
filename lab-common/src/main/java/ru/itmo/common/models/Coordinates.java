package ru.itmo.common.models;

public class Coordinates implements Validatable {
    private Integer x; // Максимальное значение поля: 793, Поле не может быть null
    private Float y; // Значение поля должно быть больше -429, Поле не может быть null

    public Coordinates() {
    }


    public Coordinates(Integer x, Float y) {
        this.x = x;
        this.y = y;
    }

    public Integer getX() {
        return this.x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Float getY() {
        return this.y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    /**
     * Validates Coordinates
     * @return returns true if Coordinates are valid
     */
    @Override
    public boolean validate() {
        if (x == null || y == null) return false;
        if (x > 793) return false;
        return y > -429;
    }
}
