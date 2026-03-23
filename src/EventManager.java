import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Manages all event operations: CRUD, registration, search, sorting.
 * Uses a HashMap to store events keyed by Event ID.
 */
public class EventManager {

    private Map<Integer, Event> events;

    public EventManager(Map<Integer, Event> events) {
        this.events = events;
    }

    public Map<Integer, Event> getEvents() { return events; }

    // --- Event CRUD (Staff operations) ---

    /**
     * Creates a new event after validating all inputs.
     */
    public void createEvent(int id, String name, String date, String time,
                            String location, int maxParticipants) throws Exception {
        if (events.containsKey(id)) {
            throw new Exception("Event ID " + id + " already exists.");
        }
        validateDate(date);
        validateTime(time);
        if (name.trim().isEmpty() || location.trim().isEmpty()) {
            throw new Exception("Event name and location cannot be empty.");
        }
        if (maxParticipants <= 0) {
            throw new Exception("Maximum participants must be a positive number.");
        }
        events.put(id, new Event(id, name, date, time, location, maxParticipants));
    }

    /**
     * Updates an existing event's name, time, or location.
     */
    public void updateEvent(int id, String newName, String newTime, String newLocation) throws Exception {
        Event e = getEventOrThrow(id);
        if (e.isCancelled()) throw new Exception("Cannot update a cancelled event.");
        if (newName != null && !newName.trim().isEmpty()) e.setEventName(newName);
        if (newTime != null && !newTime.trim().isEmpty()) {
            validateTime(newTime);
            e.setEventTime(newTime);
        }
        if (newLocation != null && !newLocation.trim().isEmpty()) e.setLocation(newLocation);
    }

    /**
     * Cancels an event by ID.
     */
    public void cancelEvent(int id) throws Exception {
        Event e = getEventOrThrow(id);
        if (e.isCancelled()) throw new Exception("Event is already cancelled.");
        e.setCancelled(true);
    }

    // --- Student operations ---

    /**
     * Registers a student for an event. Returns status message.
     */
    public String registerStudent(int eventId, String studentId) throws Exception {
        Event e = getEventOrThrow(eventId);
        if (e.isCancelled()) throw new Exception("Cannot register for a cancelled event.");
        String result = e.registerStudent(studentId);
        if (result.equals("DUPLICATE")) {
            throw new Exception("Student " + studentId + " is already registered/waitlisted for this event.");
        }
        return result;
    }

    /**
     * Cancels a student's registration. Handles waitlist promotion in a separate thread.
     */
    public void cancelStudentRegistration(int eventId, String studentId) throws Exception {
        Event e = getEventOrThrow(eventId);
        String status = e.getStudentStatus(studentId);
        if (status == null) {
            throw new Exception("Student " + studentId + " is not registered for event " + eventId + ".");
        }

        String promoted = e.cancelRegistration(studentId);

        // If a student was promoted from waitlist, handle in a separate thread
        if (promoted != null) {
            final String promotedId = promoted;
            final int evId = eventId;
            Thread promotionThread = new Thread(() -> {
                System.out.println("\n>> Registration cancelled. Student " + promotedId
                    + " has been promoted from the waitlist to the event (ID: " + evId + ").");
            });
            promotionThread.start();
            try {
                promotionThread.join(); // Wait for notification to display
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        } else {
            System.out.println("Registration cancelled successfully for student " + studentId + ".");
        }
    }

    /**
     * Returns a list of registration statuses for a given student across all events.
     */
    public List<String> getStudentStatuses(String studentId) {
        List<String> statuses = new ArrayList<>();
        for (Event e : events.values()) {
            String status = e.getStudentStatus(studentId);
            if (status != null) {
                statuses.add("Event: " + e.getEventName() + " (ID: " + e.getEventId() + ") — " + status);
            }
        }
        return statuses;
    }

    // --- Display and Search ---

    /**
     * Returns all active (non-cancelled) events.
     */
    public List<Event> getActiveEvents() {
        List<Event> active = new ArrayList<>();
        for (Event e : events.values()) {
            if (!e.isCancelled()) active.add(e);
        }
        return active;
    }

    /**
     * Returns all events including cancelled ones.
     */
    public List<Event> getAllEvents() {
        return new ArrayList<>(events.values());
    }

    /**
     * Searches events by name (partial match, case-insensitive).
     */
    public List<Event> searchByName(String keyword) {
        List<Event> results = new ArrayList<>();
        for (Event e : events.values()) {
            if (e.getEventName().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(e);
            }
        }
        return results;
    }

    /**
     * Searches events by exact date.
     */
    public List<Event> searchByDate(String date) {
        List<Event> results = new ArrayList<>();
        for (Event e : events.values()) {
            if (e.getEventDate().equals(date)) {
                results.add(e);
            }
        }
        return results;
    }

    /**
     * Sorts events by name alphabetically.
     */
    public List<Event> sortByName() {
        List<Event> sorted = getAllEvents();
        sorted.sort(Comparator.comparing(Event::getEventName, String.CASE_INSENSITIVE_ORDER));
        return sorted;
    }

    /**
     * Sorts events by date.
     */
    public List<Event> sortByDate() {
        List<Event> sorted = getAllEvents();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sorted.sort((a, b) -> {
            try {
                return sdf.parse(a.getEventDate()).compareTo(sdf.parse(b.getEventDate()));
            } catch (ParseException ex) {
                return 0;
            }
        });
        return sorted;
    }

    // --- Validation helpers ---

    private Event getEventOrThrow(int id) throws Exception {
        Event e = events.get(id);
        if (e == null) throw new Exception("Event with ID " + id + " does not exist.");
        return e;
    }

    public static void validateDate(String date) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        try {
            sdf.parse(date);
        } catch (ParseException e) {
            throw new Exception("Invalid date format. Use dd/MM/yyyy.");
        }
    }

    public static void validateTime(String time) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setLenient(false);
        try {
            sdf.parse(time);
        } catch (ParseException e) {
            throw new Exception("Invalid time format. Use HH:mm.");
        }
    }
}
