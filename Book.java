/** Represents an individual book in the library catalog. */
public class Book {
    private String bookID;
    private String title;
    private String category;
    private boolean isBorrowed;

    public Book(String bookID, String title, String category) {
        this.bookID = bookID;
        this.title = title;
        this.category = category;
        this.isBorrowed = false;
    }

    public String getBookID()   { return bookID; }
    public String getTitle()    { return title; }
    public String getCategory() { return category; }
    public boolean isBorrowed() { return isBorrowed; }

    public void setTitle(String title)       { this.title = title; }
    public void setCategory(String category) { this.category = category; }

    public String getBookDetails() {
        return String.format("%-10s %-32s %-16s %s",
                bookID, title, category, isBorrowed ? "Borrowed" : "Available");
    }

    public void updateStatus(boolean borrowed) {
        this.isBorrowed = borrowed;
    }

    public boolean checkAvailable() {
        return !isBorrowed;
    }
}