import java.io.*;
import java.util.*;

// This class handles saving and loading event data to/from text files
// So that data is not lost when the program closes
public class DataPersistence {

    // File names where we store the data
    private static final String EVENTS_FILE = "events.txt";
    private static final String REGISTRATIONS_FILE = "registrations.txt";

    // Saves all events and their registrations to text files
    public static void saveData(Map<Integer, Event> events) throws IOException {
        // Save event details to events.txt
        // Each line has the format: id|name|date|time|location|max
        try (PrintWriter pw = new PrintWriter(new FileWriter(EVENTS_FILE))) {
            for (Event e : events.values()) {
                pw.println(e.getEventId() + "|" + e.getEventName() + "|" + e.getEventDate() + "|"
                    + e.getEventTime() + "|" + e.getLocation() + "|" + e.getMaxParticipants());
            }
        }

        // Save registrations and waitlists to registrations.txt
        // Each line has the format: eventId|studentId|status
        try (PrintWriter pw = new PrintWriter(new FileWriter(REGISTRATIONS_FILE))) {
            for (Event e : events.values()) {
                // Save registered students
                for (String sid : e.getRegisteredStudents()) {
                    pw.println(e.getEventId() + "|" + sid + "|REGISTERED");
                }
                // Save waitlisted students
                for (String sid : e.getWaitlist()) {
                    pw.println(e.getEventId() + "|" + sid + "|WAITLISTED");
                }
            }
        }
    }

    // Loads all events and registrations from the text files
    // Returns a map of events with their registrations and waitlists restored
    public static Map<Integer, Event> loadData() throws IOException {
        Map<Integer, Event> events = new LinkedHashMap<>();

        // Load events from events.txt
        File eventsFile = new File(EVENTS_FILE);
        if (eventsFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(eventsFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    // Split each line by the | character
                    String[] parts = line.split("\\|");
                    if (parts.length >= 6) {
                        int id = Integer.parseInt(parts[0].trim());
                        // Create a new Event object from the file data
                        Event e = new Event(id, parts[1].trim(), parts[2].trim(),
                            parts[3].trim(), parts[4].trim(), Integer.parseInt(parts[5].trim()));
                        events.put(id, e);
                    }
                }
            }
        }

        // Load registrations and waitlists from registrations.txt
        File regFile = new File(REGISTRATIONS_FILE);
        if (regFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(regFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 3) {
                        int eventId = Integer.parseInt(parts[0].trim());
                        String studentId = parts[1].trim();
                        String status = parts[2].trim();
                        // Find the event and add the student to the right list
                        Event e = events.get(eventId);
                        if (e != null) {
                            if (status.equals("REGISTERED")) {
                                e.getRegisteredStudents().add(studentId);
                            } else {
                                e.getWaitlist().add(studentId);
                            }
                        }
                    }
                }
            }
        }

        return events;
    }
}
