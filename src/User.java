// This is the base class for all users (Staff and Student)
// It's abstract so you can't create a User directly, you have to use Staff or Student
public abstract class User {
    protected String userId;
    protected String name;
    protected String role;

    // Constructor - sets up the user with their ID, name, and role
    public User(String userId, String name, String role) {
        this.userId = userId;
        this.name = name;
        this.role = role;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getRole() { return role; }

    // Each subclass (Staff/Student) has to implement their own menu
    // This is polymorphism - same method name but different behavior
    public abstract void displayMenu();

    // How the user is displayed when printed
    @Override
    public String toString() {
        return role + " [ID=" + userId + ", Name=" + name + "]";
    }
}
