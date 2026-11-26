/** Abstract base class for everyone who uses the system (Inheritance + Abstraction). */
public abstract class User {
    private String userID;
    private String name;
    private String contactNumber;
    private String username;
    private String password;

    public User(String userID, String name, String contactNumber, String username, String password) {
        this.userID = userID;
        this.name = name;
        this.contactNumber = contactNumber;
        this.username = username;
        this.password = password;
    }

    public String getUserID()        { return userID; }
    public String getName()          { return name; }
    public String getContactNumber() { return contactNumber; }
    public String getUsername()      { return username; }

    public void setName(String name)                   { this.name = name; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setPassword(String password)           { this.password = password; }

    /** Checks the given credentials against this user's. */
    public boolean login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public void logout() {
        System.out.println("\n" + name + " has been logged out.");
    }

    /** Each role shows its own menu (Polymorphism). */
    public abstract void displayMenu();

    public abstract String getRole();
}