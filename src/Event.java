import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents a campus event with registered participant list and waitlist.
 * Uses ArrayList for registered users and LinkedList (Queue) for waitlist.
 */
public class Event {
    private int eventId;
    private String eventName;
    private String eventDate;   // dd/MM/yyyy
    private String eventTime;   // HH:mm
    private String location;
    private int maxParticipants;
    private boolean cancelled;

    // Registered participants (student IDs)
    private ArrayList<String> registeredStudents;
    // Waitlist queue (student IDs) — FIFO order
    private Queue<String> waitlist;

    public Event(int eventId, String eventName, String eventDate, String eventTime,
                 String location, int maxParticipants) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.location = location;
        this.maxParticipants = maxParticipants;
        this.cancelled = false;
        this.registeredStudents = new ArrayList<>();
        this.waitlist = new LinkedList<>();
    }

    // --- Getters and Setters ---
    public int getEventId() { return eventId; }
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }
    public String getEventTime() { return eventTime; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public int getMaxParticipants() { return maxParticipants; }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    public ArrayList<String> getRegisteredStudents() { return registeredStudents; }
    public Queue<String> getWaitlist() { return waitlist; }

    /**
     * Registers a student. Returns status: "REGISTERED", "WAITLISTED", or "DUPLICATE".
     */
    public String registerStudent(String studentId) {
        if (registeredStudents.contains(studentId) || waitlist.contains(studentId)) {
            return "DUPLICATE";
        }
        if (registeredStudents.size() < maxParticipants) {
            registeredStudents.add(studentId);
            return "REGISTERED";
        }
        waitlist.add(studentId);
        return "WAITLISTED";
    }

    /**
     * Cancels a student's registration or waitlist entry.
     * Returns the promoted student ID if promotion occurred, null otherwise.
     */
    public String cancelRegistration(String studentId) {
        if (registeredStudents.remove(studentId)) {
            // Promote first waitlisted student if available
            if (!waitlist.isEmpty()) {
                String promoted = waitlist.poll();
                registeredStudents.add(promoted);
                return promoted;
            }
            return null;
        }
        // Remove from waitlist if present
        waitlist.remove(studentId);
        return null;
    }

    /**
     * Checks if a student is registered, waitlisted, or not found.
     */
    public String getStudentStatus(String studentId) {
        if (registeredStudents.contains(studentId)) return "Registered";
        if (waitlist.contains(studentId)) return "Waitlisted";
        return null;
    }

    @Override
    public String toString() {
        return String.format(
            "ID: %d | Name: %s | Date: %s | Time: %s | Location: %s | Max: %d | Registered: %d | Waitlist: %d | Status: %s",
            eventId, eventName, eventDate, eventTime, location, maxParticipants,
            registeredStudents.size(), waitlist.size(),
            cancelled ? "CANCELLED" : "ACTIVE"
        );
    }
}
