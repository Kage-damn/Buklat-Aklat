import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Stores the details of one borrowed book. */
public class BorrowRecord {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    private String recordID;
    private String bookID;
    private String studentID;
    private LocalDate borrowDate;
    private LocalDate returnDate;   // due date
    private boolean isReturned;

    public BorrowRecord(String recordID, String bookID, String studentID, int loanDays) {
        this.recordID = recordID;
        this.bookID = bookID;
        this.studentID = studentID;
        this.borrowDate = LocalDate.now();
        this.returnDate = borrowDate.plusDays(loanDays);
        this.isReturned = false;
    }

    public String getRecordID()  { return recordID; }
    public String getBookID()    { return bookID; }
    public String getStudentID() { return studentID; }
    public boolean isReturned()  { return isReturned; }

    public void markReturned() { this.isReturned = true; }

    public boolean isOverdue() {
        return !isReturned && LocalDate.now().isAfter(returnDate);
    }

    public String getRecordDetails() {
        String status = isReturned ? "Returned" : (isOverdue() ? "OVERDUE" : "Borrowed");
        return String.format("%-8s %-10s %-10s Borrowed: %s  Due: %s  [%s]",
                recordID, bookID, studentID, borrowDate.format(FMT), returnDate.format(FMT), status);
    }

    /** Short line for the student's own view. */
    public String getDueInfo() {
        long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), returnDate);
        String note = days < 0 ? "overdue by " + (-days) + " day(s)"
                    : days == 0 ? "due today"
                    : days + " day(s) left";
        return "Borrowed: " + borrowDate.format(FMT) + "  Return by: " + returnDate.format(FMT) + "  (" + note + ")";
    }
}