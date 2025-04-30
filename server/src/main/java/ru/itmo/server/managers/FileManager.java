package ru.itmo.server.managers;

import ru.itmo.common.models.Coordinates;
import ru.itmo.common.models.Person;
import ru.itmo.common.models.Ticket;
import ru.itmo.common.models.TicketType;

import javax.xml.stream.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Set;

/**
 * Class that saves and loads Tickets from file
 */
public class FileManager {
    private XMLStreamWriter writer;
    private int indentLevel = 0;

    /**
     * Saves collection to filePath
     *
     * @param collection collection to save
     * @param filePath   path of the file to save
     */
    public void save(Set<Ticket> collection, String filePath) {
        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(filePath))) {
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            this.writer = factory.createXMLStreamWriter(stream, "UTF-8");
            writeXmlContent(collection);
            writer.close();
        } catch (XMLStreamException | IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        } finally {
            this.writer = null;
        }
    }

    /**
     * Write collection content to file
     *
     * @param collection collection to write
     * @throws XMLStreamException if there is an error in writing file
     */
    private void writeXmlContent(Set<Ticket> collection) throws XMLStreamException {
        indentLevel = 0;
        final String lineSeparator = System.lineSeparator();

        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeCharacters(lineSeparator);
        writeStartElement("tickets");

        for (Ticket ticket : collection) {
            // Start ticket element
            writeStartElement("ticket");
            writer.writeAttribute("id", String.valueOf(ticket.getId()));

            // Name
            writeElement("name", ticket.getName());

            // Coordinates
            writeStartElement("coordinates");
            writeElement("x", ticket.getCoordinates().getX().toString());
            writeElement("y", ticket.getCoordinates().getY().toString());
            writeEndElement();

            // Creation Date
            writeElement("creationDate", ticket.getCreationDate().toString());

            // Price (optional)
            if (ticket.getPrice() != null) {
                writeElement("price", ticket.getPrice().toString());
            }

            writeElement("comment", ticket.getComment());

            // Refundable
            writeElement("refundable", ticket.getRefundable().toString());

            // Type (optional)
            if (ticket.getType() != null) {
                writeElement("type", ticket.getType().name());
            }

            // Person (optional)
            if (ticket.getPerson() != null) {
                writeStartElement("person");
                Person person = ticket.getPerson();
                if (person.getBirthday() != null) {
                    writeElement("birthday", person.getBirthday().toString());
                }
                writeElement("height", person.getHeight().toString());
                writeElement("weight", String.valueOf(person.getWeight()));
                if (person.getPassportID() != null && !person.getPassportID().isEmpty()) {
                    writeElement("passportID", person.getPassportID());
                }
                writeEndElement();
            }

            // End ticket element
            writeEndElement();
        }

        // End tickets element
        writeEndElement();
        writer.writeEndDocument();
    }

    /**
     * Write start element
     *
     * @param element element to write
     * @throws XMLStreamException if there is an error in writing
     */
    private void writeStartElement(String element)
            throws XMLStreamException {
        writeIndentation();
        writer.writeStartElement(element);
        indentLevel++;
    }

    /**
     * Write end element
     */
    private void writeEndElement()
            throws XMLStreamException {
        indentLevel--;
        writeIndentation();
        writer.writeEndElement();
    }

    /**
     * Write element to file
     *
     * @param name  name of the element
     * @param value value of the element
     * @throws XMLStreamException if there is an error in writing to file
     */
    private void writeElement(String name, String value)
            throws XMLStreamException {
        writeIndentation();
        writer.writeStartElement(name);
        writer.writeCharacters(value);
        writer.writeEndElement();
    }

    /**
     * Writes to file with indentation
     *
     * @throws XMLStreamException if there is an error in writing to file
     */
    private void writeIndentation() throws XMLStreamException {
        String indent = "\t".repeat(indentLevel);
        writer.writeCharacters(System.lineSeparator() + indent);
    }

    /**
     * Loads collection from file
     *
     * @param filePath path to file
     * @return Collection
     */
    public Set<Ticket> load(String filePath) {
        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(filePath))) {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(stream);
            return new XmlParser().parse(reader);
        } catch (XMLStreamException | IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
            return new HashSet<>();
        }
    }

    /**
     * Class that parses xml files
     */
    private static class XmlParser {
        private final Set<Ticket> collection = new HashSet<>();
        private Ticket currentTicket;
        private Coordinates currentCoordinates;
        private Person currentPerson;
        private String currentElement;

        /**
         * Parses collection from file
         *
         * @param reader file reader
         * @return collection
         * @throws XMLStreamException if there is an error while reading from file
         */
        public Set<Ticket> parse(XMLStreamReader reader) throws XMLStreamException {
            while (reader.hasNext()) {
                int event = reader.next();
                try {
                    switch (event) {
                        case XMLStreamConstants.START_ELEMENT -> handleStartElement(reader);
                        case XMLStreamConstants.CHARACTERS -> handleCharacters(reader);
                        case XMLStreamConstants.END_ELEMENT -> handleEndElement(reader);
                    }
                } catch (DateTimeParseException e) {
                    System.out.println("Error parsing date: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Error parsing element: " + e.getMessage());
                }
            }
            return collection;
        }

        /**
         * parses start element
         *
         * @param reader file reader
         */
        private void handleStartElement(XMLStreamReader reader) {
            currentElement = reader.getLocalName();
            switch (currentElement) {
                case "ticket" -> initNewTicket(reader);
                case "coordinates" -> currentCoordinates = new Coordinates();
                case "person" -> currentPerson = new Person();
            }
        }

        /**
         * Parses values in ticket
         *
         * @param reader file reader
         * @throws IllegalStateException if there is an illegal parameter in file
         */
        private void handleCharacters(XMLStreamReader reader) throws IllegalStateException {
            String text = reader.getText().trim();
            if (text.isEmpty() || currentTicket == null) return;

            switch (currentElement) {
                case "name" -> currentTicket.setName(text);
                case "creationDate" -> currentTicket.setCreationDate(ZonedDateTime.parse(text));
                case "price" -> currentTicket.setPrice(Float.parseFloat(text));
                case "comment" -> currentTicket.setComment(text);
                case "refundable" -> currentTicket.setRefundable(Boolean.parseBoolean(text));
                case "type" -> currentTicket.setType(TicketType.valueOf(text));
                case "x" -> currentCoordinates.setX(Integer.parseInt(text));
                case "y" -> currentCoordinates.setY(Float.parseFloat(text));
                case "birthday" -> currentPerson.setBirthday(LocalDateTime.parse(text));
                case "height" -> currentPerson.setHeight(Integer.parseInt(text));
                case "weight" -> currentPerson.setWeight(Float.parseFloat(text));
                case "passportID" -> currentPerson.setPassportID(text);
                default -> throw new IllegalStateException("Unexpected value: " + currentElement);
            }
        }

        /**
         * parses end element
         *
         * @param reader file reader
         */
        private void handleEndElement(XMLStreamReader reader) {
            switch (reader.getLocalName()) {
                case "ticket" -> completeTicket();
                case "coordinates" -> attachCoordinates();
                case "person" -> attachPerson();
            }
        }

        /**
         * Creating new ticket instance to parse
         *
         * @param reader file reader
         */
        private void initNewTicket(XMLStreamReader reader) {
            currentTicket = new Ticket();
            try {
                currentTicket.setId(Long.parseLong(reader.getAttributeValue(null, "id")));
            } catch (NumberFormatException e) {
                System.out.println("Invalid ticket id");
            }
        }

        /**
         * Validates and add ticket to collection
         */
        private void completeTicket() {
            if (currentTicket.validate()) collection.add(currentTicket);
            else System.out.println("Invalid ticket in file");
            currentTicket = null;
        }

        /**
         * Set coordinates
         */
        private void attachCoordinates() {
            currentTicket.setCoordinates(currentCoordinates);
            currentCoordinates = null;
        }

        /**
         * Set person
         */
        private void attachPerson() {
            currentTicket.setPerson(currentPerson);
            currentPerson = null;
        }
    }
}
