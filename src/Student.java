/**
 * Student user — can view events, register, cancel registration, view status.
 */
public class Student extends User {

    public Student(String userId, String name) {
        super(userId, name, "Student");
    }

    @Override
    public void displayMenu() {
        System.out.println("\n===== STUDENT MENU =====");
        System.out.println("1. View Available Events");
        System.out.println("2. Register for Event");
        System.out.println("3. Cancel Registration");
        System.out.println("4. View My Registration Status");
        System.out.println("5. Search Events");
        System.out.println("6. Exit");
        System.out.print("Select option: ");
    }
}
