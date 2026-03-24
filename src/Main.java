import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

// This is the main class that runs the whole program
// It handles the menus for staff and students
public class Main {

    // Scanner to read user input from the console
    private static Scanner scanner = new Scanner(System.in);
    // EventManager handles all the event logic (create, update, delete, etc.)
    private static EventManager manager;

    public static void main(String[] args) {
        // Try to load any previously saved events from the file
        Map<Integer, Event> events;
        try {
            events = DataPersistence.loadData();
            System.out.println("Data loaded successfully.");
        } catch (IOException e) {
            // If no file exists, just start with an empty list
            events = new LinkedHashMap<>();
            System.out.println("No previous data found. Starting fresh.");
        }
        // Create the event manager with the loaded (or empty) events
        manager = new EventManager(events);

        // Ask the user if they are staff or student
        User user = selectRole();
        System.out.println("\nWelcome, " + user.getName() + " (" + user.getRole() + ")");

        // Show the correct menu depending on the role
        if (user instanceof Staff) {
            staffMenu();
        } else {
            studentMenu((Student) user);
        }
    }

    // This method asks the user to pick their role and enter their details
    private static User selectRole() {
        System.out.println("========================================");
        System.out.println("Welcome to Richfield Campus Management System");
        System.out.println("========================================");
        System.out.println("Select your role:");
        System.out.println("1. Staff");
        System.out.println("2. Student");

        // Keep asking until they pick 1 or 2
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

        // Get the user's ID and name
        System.out.print("Enter your User ID: ");
        String userId = scanner.nextLine().trim();
        System.out.print("Enter your Name: ");
        String name = scanner.nextLine().trim();

        // Return the correct type of user based on their choice
        if (choice == 1) return new Staff(userId, name);
        return new Student(userId, name);
    }

    // ==================== STAFF MENU ====================

    // This method shows the staff menu and handles their choices
    private static void staffMenu() {
        Staff staff = new Staff("", ""); // just used to display the menu
        boolean running = true;
        while (running) {
            staff.displayMenu();
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                // Call the right method based on what the staff picked
                switch (choice) {
                    case 1: createEvent(); break;
                    case 2: updateEvent(); break;
                    case 3: cancelEvent(); break;
                    case 4: viewAllEvents(); break;
                    case 5: viewParticipants(); break;
                    case 6: sortEvents(); break;
                    case 7: searchEvents(); break;
                    case 8: exit("Staff"); running = false; break;
                    default: System.out.println("Invalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    // This method lets staff create a new event
    // Each input is validated immediately and keeps asking until valid
    private static void createEvent() {
        try {
            // Get the event ID - must be a number
            int id;
            while (true) {
                System.out.print("Enter Event ID (unique integer): ");
                try { id = Integer.parseInt(scanner.nextLine().trim()); break; } catch (NumberFormatException e) {
                    System.out.println("Error: Event ID must be a valid integer. Try again.");
                }
            }

            // Get the event name - can't be empty
            String name;
            while (true) {
                System.out.print("Enter Event Name: ");
                name = scanner.nextLine().trim();
                if (!name.isEmpty()) break;
                System.out.println("Error: Event name cannot be empty. Try again.");
            }

            // Get the date - must match dd/MM/yyyy format
            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String date;
            while (true) {
                System.out.print("Enter Event Date (dd/MM/yyyy): ");
                date = scanner.nextLine().trim();
                try { LocalDate.parse(date, dateFmt); break; } catch (DateTimeParseException e) {
                    System.out.println("Error: Invalid date format. Use dd/MM/yyyy. Try again.");
                }
            }

            // Get the time - must match HH:mm format
            DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
            String time;
            while (true) {
                System.out.print("Enter Event Time (HH:mm): ");
                time = scanner.nextLine().trim();
                try { LocalTime.parse(time, timeFmt); break; } catch (DateTimeParseException e) {
                    System.out.println("Error: Invalid time format. Use HH:mm. Try again.");
                }
            }

            // Get the location - can't be empty
            String location;
            while (true) {
                System.out.print("Enter Location: ");
                location = scanner.nextLine().trim();
                if (!location.isEmpty()) break;
                System.out.println("Error: Location cannot be empty. Try again.");
            }

            // Get max participants - must be a positive number
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

            // Create the event and save it to the file right away
            manager.createEvent(id, name, date, time, location, max);
            DataPersistence.saveData(manager.getEvents());
            System.out.println("Event created successfully!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // This method lets staff update an existing event
    // They can skip fields by pressing Enter
    private static void updateEvent() {
        try {
            System.out.print("Enter Event ID to update: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            // Get new name (or skip)
            System.out.print("New Event Name (press Enter to skip): ");
            String name = scanner.nextLine().trim();

            // Get new date (or skip) - validates format if entered
            System.out.print("New Event Date (dd/MM/yyyy, press Enter to skip): ");
            String date = scanner.nextLine().trim();
            if (!date.isEmpty()) {
                DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                while (true) {
                    try { LocalDate.parse(date, dateFmt); break; } catch (DateTimeParseException e) {
                        System.out.println("Error: Invalid date format. Use dd/MM/yyyy. Try again.");
                        System.out.print("New Event Date (dd/MM/yyyy): ");
                        date = scanner.nextLine().trim();
                    }
                }
            }

            // Get new time (or skip) - validates format if entered
            System.out.print("New Event Time (HH:mm, press Enter to skip): ");
            String time = scanner.nextLine().trim();
            if (!time.isEmpty()) {
                DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
                while (true) {
                    try { LocalTime.parse(time, timeFmt); break; } catch (DateTimeParseException e) {
                        System.out.println("Error: Invalid time format. Use HH:mm. Try again.");
                        System.out.print("New Event Time (HH:mm): ");
                        time = scanner.nextLine().trim();
                    }
                }
            }

            // Get new location (or skip)
            System.out.print("New Location (press Enter to skip): ");
            String location = scanner.nextLine().trim();

            // Update the event and save changes to file
            manager.updateEvent(id,
                name.isEmpty() ? null : name,
                date.isEmpty() ? null : date,
                time.isEmpty() ? null : time,
                location.isEmpty() ? null : location);
            DataPersistence.saveData(manager.getEvents());
            System.out.println("Event updated successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter a valid Event ID.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // This method removes an event completely from the list
    private static void cancelEvent() {
        try {
            System.out.print("Enter Event ID to cancel: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            // Remove the event and save the updated list
            manager.cancelEvent(id);
            DataPersistence.saveData(manager.getEvents());
            System.out.println("Event removed successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter a valid Event ID.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // This method shows all events with an option to sort them first
    private static void viewAllEvents() {
        List<Event> all = manager.getAllEvents();
        if (all.isEmpty()) {
            System.out.println("No events found.");
            return;
        }
        // Ask the user how they want to sort the events
        System.out.println("Sort by: 1. Event Name  2. Event Date  3. None");
        System.out.print("Select option: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 1) all = manager.sortByName();
            else if (choice == 2) all = manager.sortByDate();
            else if (choice != 3) { System.out.println("Invalid option."); return; }
        } catch (NumberFormatException e) {
            System.out.println("Invalid option. Showing unsorted.");
        }
        // Print out all the events
        System.out.println("\n--- All Events ---");
        for (Event e : all) System.out.println(e);
    }

    // This method shows the participants and waitlist for a specific event
    private static void viewParticipants() {
        try {
            System.out.print("Enter Event ID: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            Event e = manager.getEvents().get(id);
            if (e == null) {
                System.out.println("Event not found.");
                return;
            }
            // Display the registered students and waitlist
            System.out.println("\n--- " + e.getEventName() + " ---");
            System.out.println("Registered Participants: " + e.getRegisteredStudents());
            System.out.println("Waitlist: " + new ArrayList<>(e.getWaitlist()));
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter a valid Event ID.");
        }
    }

    // This method sorts events by name or date and displays them
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

    // This method shows the student menu and handles their choices
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
                    case 6: exit("Student"); running = false; break;
                    default: System.out.println("Invalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    // Shows all events that are currently available
    private static void viewAvailableEvents() {
        List<Event> active = manager.getActiveEvents();
        if (active.isEmpty()) {
            System.out.println("No available events.");
            return;
        }
        System.out.println("\n--- Available Events ---");
        for (Event e : active) System.out.println(e);
    }

    // Lets a student register for an event by entering the event ID
    private static void registerForEvent(Student student) {
        try {
            System.out.print("Enter Event ID to register: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            String result = manager.registerStudent(id, student.getUserId());
            // Check if they got registered or put on the waitlist
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

    // Lets a student cancel their registration for an event
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

    // Shows the student which events they are registered or waitlisted for
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

    // Lets both staff and students search for events by name or date
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

            // Show results or tell the user nothing was found
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

    // Prints which menu the user is leaving and says goodbye
    private static void exit(String role) {
        System.out.println("Exiting " + role + " Menu. Goodbye!");
    }
}
