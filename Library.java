/**
 * Holds all data in fixed-size arrays and hides the borrowing logic (Abstraction).
 * Because arrays are used, the number of books/users/records is limited.
 */
public class Library {
    public static final int MAX_BOOKS = 100;
    public static final int MAX_STUDENTS = 50;
    public static final int MAX_LIBRARIANS = 10;
    public static final int MAX_RECORDS = 500;
    public static final int MAX_LOANS_PER_STUDENT = 3;
    public static final int LOAN_DAYS = 7;

    private final Book[] books = new Book[MAX_BOOKS];
    private final Student[] students = new Student[MAX_STUDENTS];
    private final Librarian[] librarians = new Librarian[MAX_LIBRARIANS];
    private final BorrowRecord[] records = new BorrowRecord[MAX_RECORDS];
    private int bookCount;
    private int studentCount;
    private int librarianCount;
    private int recordCount;

    // ---------- Lookups ----------
    public Book findBook(String bookID) {
        for (int i = 0; i < bookCount; i++) {
            if (books[i].getBookID().equalsIgnoreCase(bookID)) {
                return books[i];
            }
        }
        return null;
    }

    public Student findStudent(String studentID) {
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getStudentID().equalsIgnoreCase(studentID)) {
                return students[i];
            }
        }
        return null;
    }

    private BorrowRecord findActiveRecord(String bookID) {
        for (int i = 0; i < recordCount; i++) {
            if (records[i].getBookID().equalsIgnoreCase(bookID) && !records[i].isReturned()) {
                return records[i];
            }
        }
        return null;
    }

    private boolean isUsernameTaken(String username) {
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        for (int i = 0; i < librarianCount; i++) {
            if (librarians[i].getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    // ---------- Login ----------
    public User login(String role, String username, String password) throws LibraryException {
        if (role.equals("Student")) {
            for (int i = 0; i < studentCount; i++) {
                if (students[i].login(username, password)) {
                    return students[i];
                }
            }
        } else {
            for (int i = 0; i < librarianCount; i++) {
                if (librarians[i].login(username, password)) {
                    if (!librarians[i].isConfirmedLibrarian()) {
                        throw new LibraryException("This librarian account is not yet confirmed by an existing librarian.");
                    }
                    return librarians[i];
                }
            }
        }
        throw new LibraryException("Invalid username or password.");
    }

    // ---------- Book management ----------
    public void addBook(String bookID, String title, String category) throws LibraryException {
        if (bookID.isEmpty() || title.isEmpty() || category.isEmpty()) {
            throw new LibraryException("Book ID, title, and category are required.");
        }
        if (bookCount >= MAX_BOOKS) {
            throw new LibraryException("The book list is full (" + MAX_BOOKS + " books).");
        }
        if (findBook(bookID) != null) {
            throw new LibraryException("A book with ID " + bookID + " already exists.");
        }
        books[bookCount++] = new Book(bookID, title, category);
    }

    public void removeBook(String bookID) throws LibraryException {
        for (int i = 0; i < bookCount; i++) {
            if (books[i].getBookID().equalsIgnoreCase(bookID)) {
                if (books[i].isBorrowed()) {
                    throw new LibraryException("Cannot remove a book that is currently borrowed.");
                }
                for (int j = i; j < bookCount - 1; j++) {
                    books[j] = books[j + 1];
                }
                books[--bookCount] = null;
                return;
            }
        }
        throw new LibraryException("No book found with ID " + bookID + ".");
    }

    // ---------- Accounts ----------
    public void addStudent(String studentID, String name, String contact, String yearProgram,
                           String username, String password) throws LibraryException {
        if (studentID.isEmpty() || name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            throw new LibraryException("Student ID, name, username, and password are required.");
        }
        if (studentCount >= MAX_STUDENTS) {
            throw new LibraryException("The student list is full (" + MAX_STUDENTS + " students).");
        }
        if (findStudent(studentID) != null) {
            throw new LibraryException("A student with ID " + studentID + " already exists.");
        }
        if (isUsernameTaken(username)) {
            throw new LibraryException("That username is already taken.");
        }
        students[studentCount++] = new Student(studentID, name, contact, yearProgram, username, password);
    }

    public Librarian registerLibrarian(String name, String contact, String username, String password,
                                       boolean confirmed) throws LibraryException {
        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            throw new LibraryException("Name, username, and password are required.");
        }
        if (librarianCount >= MAX_LIBRARIANS) {
            throw new LibraryException("The librarian list is full.");
        }
        if (isUsernameTaken(username)) {
            throw new LibraryException("That username is already taken.");
        }
        Librarian librarian = new Librarian(String.format("L-%03d", librarianCount + 1),
                name, contact, username, password, confirmed);
        librarians[librarianCount++] = librarian;
        return librarian;
    }

    public Librarian[] getPendingLibrarians() {
        int n = 0;
        for (int i = 0; i < librarianCount; i++) {
            if (!librarians[i].isConfirmedLibrarian()) {
                n++;
            }
        }
        Librarian[] pending = new Librarian[n];
        int pendingCount = 0;
        for (int i = 0; i < librarianCount; i++) {
            if (!librarians[i].isConfirmedLibrarian()) {
                pending[pendingCount++] = librarians[i];
            }
        }
        return pending;
    }

    // ---------- Borrowing ----------
    public BorrowRecord borrowBook(Student student, String bookID) throws LibraryException {
        Book book = findBook(bookID);
        if (book == null) {
            throw new LibraryException("No book found with ID " + bookID + ".");
        }
        if (!book.checkAvailable()) {
            throw new LibraryException("\"" + book.getTitle() + "\" is already borrowed.");
        }
        if (student.getBorrowedBooksCount() >= MAX_LOANS_PER_STUDENT) {
            throw new LibraryException("You have reached the limit of " + MAX_LOANS_PER_STUDENT + " borrowed books.");
        }
        if (recordCount >= MAX_RECORDS) {
            throw new LibraryException("The borrow record list is full.");
        }

        BorrowRecord record = new BorrowRecord(String.format("R-%03d", recordCount + 1),
                book.getBookID(), student.getStudentID(), LOAN_DAYS);
        records[recordCount++] = record;
        book.updateStatus(true);
        student.incrementBorrowed();
        return record;
    }

    public void returnBook(Student student, String bookID) throws LibraryException {
        BorrowRecord record = findActiveRecord(bookID);
        if (record == null || !record.getStudentID().equalsIgnoreCase(student.getStudentID())) {
            throw new LibraryException("You are not currently borrowing a book with ID " + bookID + ".");
        }
        record.markReturned();
        findBook(record.getBookID()).updateStatus(false);
        student.decrementBorrowed();
    }

    // ---------- Display ----------
    public void printBookList() {
        System.out.println("\n--- BOOK LIST ---");
        if (bookCount == 0) {
            System.out.println("No books in the library yet.");
            return;
        }
        System.out.printf("%-10s %-32s %-16s %s%n", "BOOK ID", "TITLE", "CATEGORY", "STATUS");
        for (int i = 0; i < bookCount; i++) {
            System.out.println(books[i].getBookDetails());
        }
    }

    public void printStudentBorrowed(Student student) {
        System.out.println("\n--- MY BORROWED BOOKS ---");
        boolean any = false;
        for (int i = 0; i < recordCount; i++) {
            BorrowRecord record = records[i];
            if (!record.isReturned() && record.getStudentID().equalsIgnoreCase(student.getStudentID())) {
                Book book = findBook(record.getBookID());
                System.out.println("[" + record.getBookID() + "] " + (book != null ? book.getTitle() : "(removed)"));
                System.out.println("     " + record.getDueInfo());
                any = true;
            }
        }
        if (!any) System.out.println("You have no borrowed books.");
    }

    public void printMasterList() {
        System.out.println("\n--- MASTER LIST: BOOKS ---");
        if (bookCount == 0) {
            System.out.println("No books.");
        }
        for (int i = 0; i < bookCount; i++) {
            Book book = books[i];
            String line = book.getBookDetails();
            BorrowRecord record = findActiveRecord(book.getBookID());
            if (record != null) {
                Student student = findStudent(record.getStudentID());
                line += "  -> " + (student != null
                        ? student.getName() + " (" + student.getStudentID() + ")"
                        : record.getStudentID());
            }
            System.out.println(line);
        }
        System.out.println("\n--- MASTER LIST: STUDENTS ---");
        if (studentCount == 0) {
            System.out.println("No students.");
        } else {
            System.out.printf("%-12s %-26s %-18s %-14s %s%n",
                    "STUDENT ID", "NAME", "YEAR & PROGRAM", "CONTACT", "BORROWED");
        }
        for (int i = 0; i < studentCount; i++) {
            Student student = students[i];
            System.out.printf("%-12s %-26s %-18s %-14s %d%n",
                    student.getStudentID(), student.getName(), student.getYearLevelProgram(),
                    student.getContactNumber(), student.getBorrowedBooksCount());
        }
    }

    public void printBorrowRecords() {
        System.out.println("\n--- BORROWED RECORDS ---");
        if (recordCount == 0) {
            System.out.println("No borrow records yet.");
            return;
        }
        for (int i = 0; i < recordCount; i++) {
            System.out.println(records[i].getRecordDetails());
        }
    }
}