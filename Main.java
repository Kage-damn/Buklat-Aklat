import java.util.Scanner;

public class Main {
    private static final Scanner in = new Scanner(System.in);
    private static final Library library = new Library();

    public static void main(String[] args) {
        seedData();
        boolean running = true;
        while (running) {
            System.out.println("\n=================================");
            System.out.println("          BUKLAT-AKLAT           ");
            System.out.println("=================================");
            System.out.println("1. Student login");
            System.out.println("2. Librarian login");
            System.out.println("3. Librarian sign up");
            System.out.println("4. Exit");
            switch (readInt("Choose: ")) {
                case 1: loginFlow("Student"); break;
                case 2: loginFlow("Librarian"); break;
                case 3: librarianSignUp(); break;
                case 4: running = false; break;
                default: System.out.println("Invalid choice. Please pick 1-4.");
            }
        }
        System.out.println("\nThank you for using Buklat-Aklat. Goodbye!");
    }

    // ---------- Login / sign up ----------
    private static void loginFlow(String role) {
        System.out.println("\n--- " + role.toUpperCase() + " LOGIN ---");
        String username = readLine("Username: ");
        String password = readLine("Password: ");
        try {
            User user = library.login(role, username, password);   // returns Student or Librarian
            System.out.println("\nWelcome, " + user.getName() + "!");
            if (user instanceof Student) studentSession((Student) user);
            else librarianSession((Librarian) user);
        } catch (LibraryException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private static void librarianSignUp() {
        System.out.println("\n--- LIBRARIAN SIGN UP ---");
        try {
            String name = readLine("Full name: ");
            String contact = readLine("Contact no.: ");
            String username = readLine("Username: ");
            String password = readLine("Password: ");
            library.registerLibrarian(name, contact, username, password, true);
            System.out.println("Account created. You can now log in.");
        } catch (LibraryException e) {
            System.out.println("Sign up failed: " + e.getMessage());
        }
    }

    // ---------- Student session ----------
    private static void studentSession(Student student) {
        boolean active = true;
        while (active) {
            student.displayMenu();                       // polymorphic call
            switch (readInt("Choose: ")) {
                case 1: student.viewBookList(library); break;
                case 2:
                    library.printBookList();
                    try {
                        BorrowRecord r = student.requestBorrow(library, readLine("Enter Book ID to borrow: "));
                        System.out.println("Borrowed! " + r.getDueInfo());
                    } catch (LibraryException e) { System.out.println("Error: " + e.getMessage()); }
                    break;
                case 3: library.printStudentBorrowed(student); break;
                case 4:
                    library.printStudentBorrowed(student);
                    try {
                        library.returnBook(student, readLine("Enter Book ID to return: "));
                        System.out.println("Book returned. Thank you!");
                    } catch (LibraryException e) { System.out.println("Error: " + e.getMessage()); }
                    break;
                case 5: student.logout(); active = false; break;
                default: System.out.println("Invalid choice. Please pick 1-5.");
            }
        }
    }

    // ---------- Librarian session ----------
    private static void librarianSession(Librarian librarian) {
        boolean active = true;
        while (active) {
            librarian.displayMenu();                     // polymorphic call
            switch (readInt("Choose: ")) {
                case 1: library.printBookList(); break;
                case 2: addBookFlow(); break;
                case 3: removeBookFlow(); break;
                case 4: addStudentFlow(); break;
                case 5: librarian.viewMasterList(library); break;
                case 6: library.printBorrowRecords(); break;
                case 7: confirmFlow(librarian); break;
                case 8: librarian.logout(); active = false; break;
                default: System.out.println("Invalid choice. Please pick 1-8.");
            }
        }
    }

    private static void addBookFlow() {
        System.out.println("\n--- ADD BOOK ---");
        try {
            String id = readLine("Book ID no.: ");
            String title = readLine("Book title: ");
            String category = readLine("Category: ");
            library.addBook(id, title, category);
            System.out.println("Book added.");
        } catch (LibraryException e) { System.out.println("Error: " + e.getMessage()); }
    }

    private static void removeBookFlow() {
        library.printBookList();
        try {
            library.removeBook(readLine("Enter Book ID to remove: "));
            System.out.println("Book removed.");
        } catch (LibraryException e) { System.out.println("Error: " + e.getMessage()); }
    }

    private static void addStudentFlow() {
        System.out.println("\n--- ADD STUDENT ---");
        try {
            String name = readLine("Student name: ");
            String id = readLine("Student ID: ");
            String contact = readLine("Contact no.: ");
            String yp = readLine("Year level & program: ");
            String username = readLine("Username for login: ");
            String password = readLine("Password: ");
            library.addStudent(id, name, contact, yp, username, password);
            System.out.println("Student added.");
        } catch (LibraryException e) { System.out.println("Error: " + e.getMessage()); }
    }

    private static void confirmFlow(Librarian librarian) {
        Librarian[] pending = library.getPendingLibrarians();
        System.out.println("\n--- PENDING LIBRARIAN ACCOUNTS ---");
        if (pending.length == 0) { System.out.println("No pending accounts."); return; }
        for (int i = 0; i < pending.length; i++)
            System.out.println((i + 1) + ". " + pending[i].getName() + " (" + pending[i].getUsername() + ")");
        int pick = readInt("Enter number to confirm (0 to cancel): ");
        if (pick == 0) return;
        if (pick < 1 || pick > pending.length) { System.out.println("Invalid selection."); return; }
        try {
            librarian.confirmLibrarianAcct(pending[pick - 1]);
            System.out.println(pending[pick - 1].getName() + " is now confirmed.");
        } catch (LibraryException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // ---------- Input helpers (exception handling for bad input) ----------
    private static int readInt(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(in.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;      // treated as an invalid menu choice
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return in.nextLine().trim();
    }

    // ---------- Sample data so the app can be tried immediately ----------
    private static void seedData() {
        try {
            library.registerLibrarian("Head Librarian", "09170000000", "admin", "admin123", true);
            library.addStudent("25-00001", "Juan Dela Cruz", "09171234567", "1st Year BSIT", "juan", "pass123");
            library.addBook("B-001", "Noli Me Tangere", "Fiction");
            library.addBook("B-002", "Introduction to Java", "Technology");
            library.addBook("B-003", "Philippine History", "History");
            library.addBook("B-004", "Discrete Mathematics", "Mathematics");
        } catch (LibraryException e) {
            System.out.println("Seed error: " + e.getMessage());
        }
    }
}