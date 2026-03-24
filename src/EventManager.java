import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

// This class handles all the event operations like creating, updating, deleting,
// registering students, searching and sorting events
public class EventManager {

    // HashMap to store all events, the key is the event ID
    private Map<Integer, Event> events;

    // Constructor - takes in the events map (could be loaded from file or empty)
    public EventManager(Map<Integer, Event> events) {
        this.events = events;
    }

    // Getter for the events map
    public Map<Integer, Event> getEvents() { return events; }

    // ==================== STAFF OPERATIONS ====================

    // Creates a new event after checking that the ID doesn't already exist
    // and that all the inputs are valid
    public void createEvent(int id, String name, String date, String time,
                            String location, int maxParticipants) throws Exception {
        // Make sure the ID is unique
        if (events.containsKey(id)) {
            throw new Exception("Event ID " + id + " already exists.");
        }
        // Validate the date and time formats
        validateDate(date);
        validateTime(time);
        // Make sure name and location aren't empty
        if (name.trim().isEmpty() || location.trim().isEmpty()) {
            throw new Exception("Event name and location cannot be empty.");
        }
        // Max participants must be positive
        if (maxParticipants <= 0) {
            throw new Exception("Maximum participants must be a positive number.");
        }
        // All good, add the event to the map
        events.put(id, new Event(id, name, date, time, location, maxParticipants));
    }

    // Updates an existing event - only changes the fields that are not null
    public void updateEvent(int id, String newName, String newDate, String newTime, String newLocation) throws Exception {
        // Find the event or throw an error if it doesn't exist
        Event e = getEventOrThrow(id);
        if (newName != null && !newName.trim().isEmpty()) e.setEventName(newName);
        if (newDate != null && !newDate.trim().isEmpty()) {
            validateDate(newDate);
            e.setEventDate(newDate);
        }
        if (newTime != null && !newTime.trim().isEmpty()) {
            validateTime(newTime);
            e.setEventTime(newTime);
        }
        if (newLocation != null && !newLocation.trim().isEmpty()) e.setLocation(newLocation);
    }

    // Removes an event completely from the list
    public void cancelEvent(int id) throws Exception {
        // Check if the event exists first
        getEventOrThrow(id);
        // Remove it from the map
        events.remove(id);
    }

    // ==================== STUDENT OPERATIONS ====================

    // Registers a student for an event
    // Returns "REGISTERED" or "WAITLISTED" depending on if there's space
    public String registerStudent(int eventId, String studentId) throws Exception {
        Event e = getEventOrThrow(eventId);
        String result = e.registerStudent(studentId);
        // If the student is already registered, throw an error
        if (result.equals("DUPLICATE")) {
            throw new Exception("Student " + studentId + " is already registered/waitlisted for this event.");
        }
        return result;
    }

    // Cancels a student's registration
    // If someone was on the waitlist, they get promoted automatically
    public void cancelStudentRegistration(int eventId, String studentId) throws Exception {
        Event e = getEventOrThrow(eventId);
        // Check if the student is actually registered
        String status = e.getStudentStatus(studentId);
        if (status == null) {
            throw new Exception("Student " + studentId + " is not registered for event " + eventId + ".");
        }

        // Cancel the registration and check if anyone got promoted from waitlist
        String promoted = e.cancelRegistration(studentId);

        // If someone got promoted, use a thread to print the notification
        if (promoted != null) {
            final String promotedId = promoted;
            final int evId = eventId;
            Thread promotionThread = new Thread(() -> {
                System.out.println("\n>> Registration cancelled. Student " + promotedId
                    + " has been promoted from the waitlist to the event (ID: " + evId + ").");
            });
            promotionThread.start();
            try {
                promotionThread.join(); // Wait for the thread to finish printing
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        } else {
            System.out.println("Registration cancelled successfully for student " + studentId + ".");
        }
    }

    // Gets a list of all events a student is registered or waitlisted for
    public List<String> getStudentStatuses(String studentId) {
        List<String> statuses = new ArrayList<>();
        // Loop through all events and check if the student is in any of them
        for (Event e : events.values()) {
            String status = e.getStudentStatus(studentId);
            if (status != null) {
                statuses.add("Event: " + e.getEventName() + " (ID: " + e.getEventId() + ") — " + status);
            }
        }
        return statuses;
    }

    // ==================== DISPLAY AND SEARCH ====================

    // Returns all events (since cancelled ones are removed, these are all active)
    public List<Event> getActiveEvents() {
        return new ArrayList<>(events.values());
    }

    // Returns all events as a list
    public List<Event> getAllEvents() {
        return new ArrayList<>(events.values());
    }

    // Searches for events by name - does a partial match (case-insensitive)
    // e.g. searching "award" would find "Awards Day"
    public List<Event> searchByName(String keyword) {
        List<Event> results = new ArrayList<>();
        for (Event e : events.values()) {
            if (e.getEventName().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(e);
            }
        }
        return results;
    }

    // Searches for events by exact date match
    public List<Event> searchByDate(String date) {
        List<Event> results = new ArrayList<>();
        for (Event e : events.values()) {
            if (e.getEventDate().equals(date)) {
                results.add(e);
            }
        }
        return results;
    }

    // Sorts events alphabetically by name (case-insensitive)
    public List<Event> sortByName() {
        List<Event> sorted = getAllEvents();
        sorted.sort(Comparator.comparing(Event::getEventName, String.CASE_INSENSITIVE_ORDER));
        return sorted;
    }

    // Sorts events by date (earliest first)
    public List<Event> sortByDate() {
        List<Event> sorted = getAllEvents();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sorted.sort((a, b) -> {
            try {
                return sdf.parse(a.getEventDate()).compareTo(sdf.parse(b.getEventDate()));
            } catch (ParseException ex) {
                return 0; // if parsing fails, just treat them as equal
            }
        });
        return sorted;
    }

    // ==================== HELPER METHODS ====================

    // Finds an event by ID or throws an error if it doesn't exist
    private Event getEventOrThrow(int id) throws Exception {
        Event e = events.get(id);
        if (e == null) throw new Exception("Event with ID " + id + " does not exist.");
        return e;
    }

    // Checks if a date string is in the correct dd/MM/yyyy format
    public static void validateDate(String date) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false); // strict mode - won't accept things like 32/01/2024
        try {
            sdf.parse(date);
        } catch (ParseException e) {
            throw new Exception("Invalid date format. Use dd/MM/yyyy.");
        }
    }

    // Checks if a time string is in the correct HH:mm format
    public static void validateTime(String time) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setLenient(false); // strict mode
        try {
            sdf.parse(time);
        } catch (ParseException e) {
            throw new Exception("Invalid time format. Use HH:mm.");
        }
    }
}
