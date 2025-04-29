package ru.itmo.common.models.builders;

import ru.itmo.common.exceptions.FieldMustNotBeEmpty;
import ru.itmo.common.models.Person;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Class for building Persons
 */
public class PersonBuilder extends Builder<Person> {
    private final Scanner scanner;

    public PersonBuilder(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Builds Person
     * @return Person that was built
     */
    @Override
    public Person build() {
        LocalDateTime birthday = readBirthday();
        Integer height = readHeight();
        float weight = readWeight();
        String passportID = readPassportID();
        return new Person(birthday, height, weight, passportID);
    }

    private String readPassportID() {
        System.out.println("Enter Passport ID (nullable): ");
        String passportID = scanner.nextLine().trim();
        if (passportID.isEmpty()) {
            return null;
        }
        return passportID;
    }

    private float readWeight() {
        while (true) {
            System.out.println("Enter weight (kg) (not nullable): ");
            String input = scanner.nextLine().trim();
            try {
                if (input.isEmpty()) {
                    throw new FieldMustNotBeEmpty("weight");
                }
                float weight = Float.parseFloat(input);
                if (weight <= 0) {
                    System.out.println("Weight must be positive!");
                    continue;
                }
                return weight;
            } catch (FieldMustNotBeEmpty e) {
                System.out.println(e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Weight must be float");
            }
        }
    }

    private Integer readHeight() {
        while (true) {
            System.out.println("Please enter the height of the person (not nullable):");
            String input = scanner.nextLine().trim();
            try {
                if (input.isEmpty()) {
                    throw new FieldMustNotBeEmpty("height");
                }
                int height = Integer.parseInt(input);
                if (height <= 0) {
                    System.out.println("Height must be positive");
                }
                return height;
            } catch (FieldMustNotBeEmpty e) {
                System.out.println(e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Height must be an integer");
            }
        }
    }

    private LocalDateTime readBirthday() {
        while (true) {
            System.out.println("Please enter persons birthday (nullable) (yyyy-MM-dd): ");
            String birthday = scanner.nextLine().trim();
            if (birthday.isEmpty()) {
                return null;
            }

            try {
                LocalDate birthdayDate = LocalDate.parse(birthday);

                if (birthdayDate.isAfter(LocalDate.now())) {
                    System.out.println("Please enter a valid birthday");
                    continue;
                }

                return birthdayDate.atStartOfDay();

            } catch (DateTimeParseException e) {
                System.out.println("Please enter a valid birthday");
            }
        }
    }
}
