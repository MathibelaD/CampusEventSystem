// Student class - extends User
// Students can view events, register, cancel registration, and check their status
public class Student extends User {

    // Constructor - calls the parent (User) constructor with "Student" as the role
    public Student(String userId, String name) {
        super(userId, name, "Student");
    }

    // Displays the student menu options
    // This overrides the abstract method from User (polymorphism)
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
