import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Main entry point for the Campus Event Management System.
 * Handles role selection and menu-driven console interaction.
 */
public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static EventManager manager;

    public static void main(String[] args) {
        // Load persisted data on startup
        Map<Integer, Event> events;
        try {
            events = DataPersistence.loadData();
            System.out.println("Data loaded successfully.");
        } catch (IOException e) {
            events = new LinkedHashMap<>();
            System.out.println("No previous data found. Starting fresh.");
        }
        manager = new EventManager(events);

        // Role selection
        User user = selectRole();
        System.out.println("\nWelcome, " + user.getName() + " (" + user.getRole() + ")");

        // Main menu loop based on role
        if (user instanceof Staff) {
            staffMenu();
        } else {
            studentMenu((Student) user);
        }
    }

    /**
     * Prompts user to select a role and enter their details.
     */
    private static User selectRole() {
        System.out.println("========================================");
        System.out.println(" CAMPUS EVENT MANAGEMENT SYSTEM");
        System.out.println("========================================");
        System.out.println("Select your role:");
        System.out.println("1. Staff");
        System.out.println("2. Student");

        int choice = 0;
        while (choice != 1 && choice != 2) {
            System.out.print("Enter choice (1 or 2): ");
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice != 1 && choice != 2) System.out.println("Invalid choice.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        System.out.print("Enter your User ID: ");
        String userId = scanner.nextLine().trim();
        System.out.print("Enter your Name: ");
        String name = scanner.nextLine().trim();

        if (choice == 1) return new Staff(userId, name);
        return new Student(userId, name);
    }

    // ==================== STAFF MENU ====================

    private static void staffMenu() {
        Staff staff = null; // menu display only
        staff = new Staff("", "");
        boolean running = true;
        while (running) {
            staff.displayMenu();
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1: createEvent(); break;
                    case 2: updateEvent(); break;
                    case 3: cancelEvent(); break;
                    case 4: viewAllEvents(); break;
                    case 5: viewParticipants(); break;
                    case 6: sortEvents(); break;
                    case 7: searchEvents(); break;
                    case 8: saveAndExit(); running = false; break;
                    default: System.out.println("Invalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static void createEvent() {
        try {
            int id = 1;
            for (int key : manager.getEvents().keySet()) { if (key >= id) id = key + 1; }
            System.out.println("Auto-generated Event ID: " + String.format("%02d", id));

            String name;
            while (true) {
                System.out.print("Enter Event Name: ");
                name = scanner.nextLine().trim();
                if (!name.isEmpty()) break;
                System.out.println("Error: Event name cannot be empty. Try again.");
            }

            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String date;
            while (true) {
                System.out.print("Enter Event Date (dd/MM/yyyy): ");
                date = scanner.nextLine().trim();
                try { LocalDate.parse(date, dateFmt); break; } catch (DateTimeParseException e) {
                    System.out.println("Error: Invalid date format. Use dd/MM/yyyy. Try again.");
                }
            }

            DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
            String time;
            while (true) {
                System.out.print("Enter Event Time (HH:mm): ");
                time = scanner.nextLine().trim();
                try { LocalTime.parse(time, timeFmt); break; } catch (DateTimeParseException e) {
                    System.out.println("Error: Invalid time format. Use HH:mm. Try again.");
                }
            }

            String location;
            while (true) {
                System.out.print("Enter Location: ");
                location = scanner.nextLine().trim();
                if (!location.isEmpty()) break;
                System.out.println("Error: Location cannot be empty. Try again.");
            }

            int max;
            while (true) {
                System.out.print("Enter Maximum Participants: ");
                try {
                    max = Integer.parseInt(scanner.nextLine().trim());
                    if (max > 0) break;
                    System.out.println("Error: Max participants must be greater than 0. Try again.");
                } catch (NumberFormatException e) {
                    System.out.println("Error: Max participants must be a valid integer. Try again.");
                }
            }

            manager.createEvent(id, name, date, time, location, max);
            System.out.println("Event created successfully!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void updateEvent() {
        try {
            System.out.print("Enter Event ID to update: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("New Event Name (press Enter to skip): ");
            String name = scanner.nextLine().trim();
            System.out.print("New Event Time (HH:mm, press Enter to skip): ");
            String time = scanner.nextLine().trim();
            System.out.print("New Location (press Enter to skip): ");
            String location = scanner.nextLine().trim();

            manager.updateEvent(id,
                name.isEmpty() ? null : name,
                time.isEmpty() ? null : time,
                location.isEmpty() ? null : location);
            System.out.println("Event updated successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter a valid Event ID.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void cancelEvent() {
        try {
            System.out.print("Enter Event ID to cancel: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            manager.cancelEvent(id);
            System.out.println("Event cancelled successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter a valid Event ID.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewAllEvents() {
        List<Event> all = manager.getAllEvents();
        if (all.isEmpty()) {
            System.out.println("No events found.");
            return;
        }
        System.out.println("\n--- All Events ---");
        for (Event e : all) System.out.println(e);
    }

    private static void viewParticipants() {
        try {
            System.out.print("Enter Event ID: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            Event e = manager.getEvents().get(id);
            if (e == null) {
                System.out.println("Event not found.");
                return;
            }
            System.out.println("\n--- " + e.getEventName() + " ---");
            System.out.println("Registered Participants: " + e.getRegisteredStudents());
            System.out.println("Waitlist: " + new ArrayList<>(e.getWaitlist()));
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter a valid Event ID.");
        }
    }

    private static void sortEvents() {
        System.out.println("Sort by: 1. Event Name  2. Event Date");
        System.out.print("Select option: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            List<Event> sorted;
            if (choice == 1) sorted = manager.sortByName();
            else if (choice == 2) sorted = manager.sortByDate();
            else { System.out.println("Invalid option."); return; }

            System.out.println("\n--- Sorted Events ---");
            for (Event e : sorted) System.out.println(e);
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter a valid number.");
        }
    }

    // ==================== STUDENT MENU ====================

    private static void studentMenu(Student student) {
        boolean running = true;
        while (running) {
            student.displayMenu();
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1: viewAvailableEvents(); break;
                    case 2: registerForEvent(student); break;
                    case 3: cancelRegistration(student); break;
                    case 4: viewMyStatus(student); break;
                    case 5: searchEvents(); break;
                    case 6: saveAndExit(); running = false; break;
                    default: System.out.println("Invalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static void viewAvailableEvents() {
        List<Event> active = manager.getActiveEvents();
        if (active.isEmpty()) {
            System.out.println("No available events.");
            return;
        }
        System.out.println("\n--- Available Events ---");
        for (Event e : active) System.out.println(e);
    }

    private static void registerForEvent(Student student) {
        try {
            System.out.print("Enter Event ID to register: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            String result = manager.registerStudent(id, student.getUserId());
            if (result.equals("REGISTERED")) {
                System.out.println("You have been successfully registered for the event!");
            } else {
                System.out.println("Event is full. You have been placed on the waitlist.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter a valid Event ID.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void cancelRegistration(Student student) {
        try {
            System.out.print("Enter Event ID to cancel registration: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            manager.cancelStudentRegistration(id, student.getUserId());
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter a valid Event ID.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewMyStatus(Student student) {
        List<String> statuses = manager.getStudentStatuses(student.getUserId());
        if (statuses.isEmpty()) {
            System.out.println("You are not registered for any events.");
            return;
        }
        System.out.println("\n--- Your Registration Status ---");
        for (String s : statuses) System.out.println(s);
    }

    // ==================== SHARED ====================

    private static void searchEvents() {
        System.out.println("Search by: 1. Event Name  2. Event Date");
        System.out.print("Select option: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            List<Event> results;
            if (choice == 1) {
                System.out.print("Enter event name (or keyword): ");
                results = manager.searchByName(scanner.nextLine().trim());
            } else if (choice == 2) {
                System.out.print("Enter event date (dd/MM/yyyy): ");
                results = manager.searchByDate(scanner.nextLine().trim());
            } else {
                System.out.println("Invalid option.");
                return;
            }

            if (results.isEmpty()) {
                System.out.println("No events found matching your search.");
            } else {
                System.out.println("\n--- Search Results ---");
                for (Event e : results) System.out.println(e);
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter a valid number.");
        }
    }

    private static void saveAndExit() {
        try {
            DataPersistence.saveData(manager.getEvents());
            System.out.println("Data saved successfully. Goodbye!");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }
}
