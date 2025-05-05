package ru.itmo.common.models;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class Ticket implements Comparable<Ticket>, Validatable, Serializable {
    private static long idCounter = 1; // Значение поля должно быть больше 0, Значение этого поля должно быть
    // уникальным, Значение этого поля должно генерироваться автоматически
    private long id;
    private String name; // Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; // Поле не может быть null
    private java.time.ZonedDateTime creationDate; // Поле не может быть null, Значение этого поля должно генерироваться
    // автоматически
    private Float price; // Поле может быть null, Значение поля должно быть больше 0
    private String comment; // Длина строки не должна быть больше 855, Поле может быть null
    private Boolean refundable; // Поле не может быть null
    private TicketType type; // Поле может быть null
    private Person person; // Поле может быть null

    public Ticket() {
        this.id = idCounter++;
    }

    public Ticket(Ticket ticket) {
        this.id = idCounter++;
        this.name = ticket.getName();
        this.coordinates = ticket.getCoordinates();
        this.creationDate = ticket.getCreationDate();
        this.price = ticket.getPrice();
        this.comment = ticket.getComment();
        this.refundable = ticket.getRefundable();
        this.type = ticket.getType();
        this.person = ticket.getPerson();
    }

    public Ticket(String name, Coordinates coordinates, ZonedDateTime creationDate, Float price,
                  String comment, Boolean refundable, TicketType type, Person person) {
        this.id = idCounter++;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.price = price;
        this.comment = comment;
        this.refundable = refundable;
        this.type = type;
        this.person = person;
    }


    public static void setIdCounter(long idCounter) {
        Ticket.idCounter = idCounter;
    }

    public void update(Ticket ticket) {
        this.name = ticket.getName();
        this.coordinates = ticket.getCoordinates();
        this.price = ticket.getPrice();
        this.comment = ticket.getComment();
        this.refundable = ticket.getRefundable();
        this.type = ticket.getType();
        this.person = ticket.getPerson();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getRefundable() {
        return refundable;
    }

    public void setRefundable(Boolean refundable) {
        this.refundable = refundable;
    }

    public TicketType getType() {
        return type;
    }

    public void setType(TicketType type) {
        this.type = type;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public int compareTo(Ticket other) {
        if (this.price == null && other.price == null) return 0;
        if (this.price == null) return -1;
        if (other.price == null) return 1;
        return Float.compare(this.price, other.price);
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "name='" + name + '\'' +
                "id='" + id + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", price=" + price +
                ", comment='" + comment + '\'' +
                ", refundable=" + refundable +
                ", type=" + type +
                ", person=" + person +
                '}';
    }

    /**
     * Validates Ticket
     *
     * @return returns true if all fields in Ticket are valid
     */
    @Override
    public boolean validate() {
        if (id <= 0) return false;
        if (name == null || name.isEmpty()) return false;
        if (coordinates == null || !coordinates.validate()) return false;
        if (creationDate == null || creationDate.isAfter(ZonedDateTime.now())) return false;
        if (price != null && price <= 0) return false;
        if (comment != null && comment.length() <= 855) return false;
        if (refundable == null) return false;
        if (person != null) return person.validate();
        return true;
    }
}
