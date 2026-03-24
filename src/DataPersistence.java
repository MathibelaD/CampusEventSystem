import java.io.*;
import java.util.*;

/**
 * Handles saving and loading of event data, registrations, and waitlists to/from files.
 */
public class DataPersistence {

    private static final String EVENTS_FILE = "events.txt";
    private static final String REGISTRATIONS_FILE = "registrations.txt";

    /**
     * Saves all events, registrations, and waitlists to files.
     */
    public static void saveData(Map<Integer, Event> events) throws IOException {
        // Save event details
        try (PrintWriter pw = new PrintWriter(new FileWriter(EVENTS_FILE))) {
            for (Event e : events.values()) {
                // Format: id|name|date|time|location|max|cancelled
                pw.println(e.getEventId() + "|" + e.getEventName() + "|" + e.getEventDate() + "|"
                    + e.getEventTime() + "|" + e.getLocation() + "|" + e.getMaxParticipants());
            }
        }

        // Save registrations and waitlists
        try (PrintWriter pw = new PrintWriter(new FileWriter(REGISTRATIONS_FILE))) {
            for (Event e : events.values()) {
                // Registered students
                for (String sid : e.getRegisteredStudents()) {
                    pw.println(e.getEventId() + "|" + sid + "|REGISTERED");
                }
                // Waitlisted students
                for (String sid : e.getWaitlist()) {
                    pw.println(e.getEventId() + "|" + sid + "|WAITLISTED");
                }
            }
        }
    }

    /**
     * Loads all events, registrations, and waitlists from files.
     */
    public static Map<Integer, Event> loadData() throws IOException {
        Map<Integer, Event> events = new LinkedHashMap<>();

        // Load events
        File eventsFile = new File(EVENTS_FILE);
        if (eventsFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(eventsFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 6) {
                        int id = Integer.parseInt(parts[0].trim());
                        Event e = new Event(id, parts[1].trim(), parts[2].trim(),
                            parts[3].trim(), parts[4].trim(), Integer.parseInt(parts[5].trim()));
                        events.put(id, e);
                    }
                }
            }
        }

        // Load registrations and waitlists
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
