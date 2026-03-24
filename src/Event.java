import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

// This class represents a single event on campus
// It stores the event details, the list of registered students, and a waitlist
public class Event {
    private int eventId;
    private String eventName;
    private String eventDate;   // stored as dd/MM/yyyy
    private String eventTime;   // stored as HH:mm
    private String location;
    private int maxParticipants;

    // ArrayList to store the IDs of students who are registered
    private ArrayList<String> registeredStudents;
    // LinkedList used as a Queue (FIFO) for the waitlist
    // First person to join the waitlist is the first to get promoted
    private Queue<String> waitlist;

    // Constructor - sets up a new event with all the details
    public Event(int eventId, String eventName, String eventDate, String eventTime,
                 String location, int maxParticipants) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.location = location;
        this.maxParticipants = maxParticipants;
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
    public ArrayList<String> getRegisteredStudents() { return registeredStudents; }
    public Queue<String> getWaitlist() { return waitlist; }

    // Registers a student for this event
    // If the event is full, they go on the waitlist instead
    // Returns "DUPLICATE" if they're already registered or waitlisted
    public String registerStudent(String studentId) {
        // Check if the student is already in the event or waitlist
        if (registeredStudents.contains(studentId) || waitlist.contains(studentId)) {
            return "DUPLICATE";
        }
        // If there's still space, register them
        if (registeredStudents.size() < maxParticipants) {
            registeredStudents.add(studentId);
            return "REGISTERED";
        }
        // Otherwise, add them to the waitlist
        waitlist.add(studentId);
        return "WAITLISTED";
    }

    // Cancels a student's registration
    // If they were registered and someone is on the waitlist,
    // the first person on the waitlist gets promoted to registered
    // Returns the promoted student's ID, or null if no one was promoted
    public String cancelRegistration(String studentId) {
        if (registeredStudents.remove(studentId)) {
            // Check if there's anyone waiting to take their spot
            if (!waitlist.isEmpty()) {
                String promoted = waitlist.poll(); // gets and removes the first person
                registeredStudents.add(promoted);
                return promoted;
            }
            return null;
        }
        // If they weren't registered, try removing from waitlist
        waitlist.remove(studentId);
        return null;
    }

    // Checks what status a student has for this event
    // Returns "Registered", "Waitlisted", or null if they're not in the event
    public String getStudentStatus(String studentId) {
        if (registeredStudents.contains(studentId)) return "Registered";
        if (waitlist.contains(studentId)) return "Waitlisted";
        return null;
    }

    // This is what gets printed when you do System.out.println(event)
    // Shows all the important info about the event in one line
    @Override
    public String toString() {
        return String.format(
            "ID: %d | Name: %s | Date: %s | Time: %s | Location: %s | Max: %d | Registered: %d | Waitlist: %d",
            eventId, eventName, eventDate, eventTime, location, maxParticipants,
            registeredStudents.size(), waitlist.size()
        );
    }
}
