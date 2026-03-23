/**
 * Abstract base class representing a user in the Campus Event Management System.
 * Supports role-based access control via polymorphism.
 */
public abstract class User {
    protected String userId;
    protected String name;
    protected String role;

    public User(String userId, String name, String role) {
        this.userId = userId;
        this.name = name;
        this.role = role;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getRole() { return role; }

    // Polymorphic method — each role defines its own menu
    public abstract void displayMenu();

    @Override
    public String toString() {
        return role + " [ID=" + userId + ", Name=" + name + "]";
    }
}
