/** A librarian with access to the management features. */
public class Librarian extends User {
    private String librarianID;
    private boolean confirmedLibrarian;

    public Librarian(String librarianID, String name, String contactNumber,
                     String username, String password, boolean confirmed) {
        super(librarianID, name, contactNumber, username, password);
        this.librarianID = librarianID;
        this.confirmedLibrarian = confirmed;
    }

    public String getLibrarianID()        { return librarianID; }
    public boolean isConfirmedLibrarian() { return confirmedLibrarian; }

    public void viewMasterList(Library library) {
        library.printMasterList();
    }

    /** Confirms a pending librarian account. */
    public void confirmLibrarianAcct(Librarian pending) throws LibraryException {
        if (pending.confirmedLibrarian) {
            throw new LibraryException(pending.getName() + " is already confirmed.");
        }
        pending.confirmedLibrarian = true;
    }

    @Override
    public void displayMenu() {
        System.out.println("\n===== LIBRARIAN MENU =====");
        System.out.println("Logged in as: " + getName() + " (" + librarianID + ")");
        System.out.println("1. View book list");
        System.out.println("2. Add a book");
        System.out.println("3. Remove a book");
        System.out.println("4. Add a student");
        System.out.println("5. Master list (books and borrowers)");
        System.out.println("6. View borrowed records");
        System.out.println("7. Confirm librarian accounts");
        System.out.println("8. Logout");
    }

    @Override
    public String getRole() { return "Librarian"; }
}