// Staff class - extends User
// Staff members can create, update, cancel events and view participants
public class Staff extends User {

    // Constructor - calls the parent (User) constructor with "Staff" as the role
    public Staff(String userId, String name) {
        super(userId, name, "Staff");
    }

    // Displays the staff menu options
    // This overrides the abstract method from User (polymorphism)
    @Override
    public void displayMenu() {
        System.out.println("\n===== STAFF MENU =====");
        System.out.println("1. Create Event");
        System.out.println("2. Update Event");
        System.out.println("3. Cancel Event");
        System.out.println("4. View All Events");
        System.out.println("5. View Participants & Waitlist");
        System.out.println("6. Sort Events");
        System.out.println("7. Search Events");
        System.out.println("8. Exit");
        System.out.print("Select option: ");
    }
}
