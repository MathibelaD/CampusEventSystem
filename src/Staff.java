/**
 * Staff user — can create, update, cancel events and view participants.
 */
public class Staff extends User {

    public Staff(String userId, String name) {
        super(userId, name, "Staff");
    }

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
        System.out.println("8. Save & Exit");
        System.out.print("Select option: ");
    }
}
