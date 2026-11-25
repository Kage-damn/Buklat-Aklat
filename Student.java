/** A student who can view books and borrow them. */
public class Student extends User {
    private String studentID;
    private String yearLevelProgram;
    private int borrowedBooksCount;

    public Student(String studentID, String name, String contactNumber,
                   String yearLevelProgram, String username, String password) {
        super(studentID, name, contactNumber, username, password);
        this.studentID = studentID;
        this.yearLevelProgram = yearLevelProgram;
        this.borrowedBooksCount = 0;
    }

    public String getStudentID()        { return studentID; }
    public String getYearLevelProgram() { return yearLevelProgram; }
    public int getBorrowedBooksCount()  { return borrowedBooksCount; }

    public void incrementBorrowed() { borrowedBooksCount++; }
    public void decrementBorrowed() { if (borrowedBooksCount > 0) borrowedBooksCount--; }

    public void viewBookList(Library library) {
        library.printBookList();
    }

    /** Requests to borrow a book; the Library enforces the rules. */
    public BorrowRecord requestBorrow(Library library, String bookID) throws LibraryException {
        return library.borrowBook(this, bookID);
    }

    @Override
    public void displayMenu() {
        System.out.println("\n===== STUDENT MENU =====");
        System.out.println("Logged in as: " + getName() + " (" + studentID + ")");
        System.out.println("1. View book list");
        System.out.println("2. Request to borrow a book");
        System.out.println("3. View my borrowed books and dates");
        System.out.println("4. Return a book");
        System.out.println("5. Logout");
    }

    @Override
    public String getRole() { return "Student"; }
}