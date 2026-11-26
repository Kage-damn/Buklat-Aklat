/** Custom checked exception for library rule violations (Exception Handling). */
public class LibraryException extends Exception {
    private static final long serialVersionUID = 1L;

    public LibraryException(String message) {
        super(message);
    }
}