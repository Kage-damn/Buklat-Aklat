import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Graphical interface for Buklat-Aklat.
 * Separate from Main.java: it only uses the existing classes (Library, Student,
 * Librarian, ...) and does not change them. Run this class instead of Main.
 */
public class LibraryGUI extends JFrame {
    private static final Font MONO = new Font(Font.MONOSPACED, Font.PLAIN, 13);
    private static final Font TITLE = new Font(Font.SANS_SERIF, Font.BOLD, 26);

    private final Library library = new Library();
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    private JTextField loginUser;
    private JPasswordField loginPass;
    private JComboBox<String> loginRole;

    public LibraryGUI() {
        super("Buklat-Aklat Library System");
        seedData();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(loadLogo(64));
        root.add(buildLoginPanel(), "login");
        setContentPane(root);
        setSize(860, 620);
        setLocationRelativeTo(null);
    }

    // ---------- Images (drawn in code, so no image files are needed) ----------
    /** A stack of three books with a bookmark, drawn at the given size (in pixels). */
    private static BufferedImage drawLogo(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        double u = size / 100.0;

        // round background
        g.setPaint(new GradientPaint(0, 0, new Color(0x2F5D8A), 0, size, new Color(0x1B3A57)));
        g.fill(new RoundRectangle2D.Double(0, 0, size, size, 24 * u, 24 * u));

        // three books: {x, y, width, height, color}
        Color[] colors = {new Color(0xE4572E), new Color(0xF3A712), new Color(0x76B041)};
        double[][] books = {{16, 64, 68, 16}, {22, 46, 58, 16}, {18, 28, 62, 16}};
        for (int i = 0; i < 3; i++) {
            double x = books[i][0] * u, y = books[i][1] * u, w = books[i][2] * u, h = books[i][3] * u;
            g.setColor(colors[i]);
            g.fill(new RoundRectangle2D.Double(x, y, w, h, 5 * u, 5 * u));
            g.setColor(new Color(255, 255, 255, 190));          // pages
            g.fill(new RoundRectangle2D.Double(x + 6 * u, y + 3 * u, w - 10 * u, h - 6 * u, 3 * u, 3 * u));
            g.setColor(colors[i].darker());                     // spine
            g.fill(new RoundRectangle2D.Double(x, y, 7 * u, h, 5 * u, 5 * u));
        }

        // bookmark ribbon
        int[] bx = {(int) (70 * u), (int) (80 * u), (int) (80 * u), (int) (75 * u), (int) (70 * u)};
        int[] by = {(int) (12 * u), (int) (12 * u), (int) (36 * u), (int) (30 * u), (int) (36 * u)};
        g.setColor(new Color(0xD7263D));
        g.fillPolygon(bx, by, 5);
        g.dispose();
        return img;
    }

    /** Uses logo.png (or logo.jpg) from the program's folder if it exists, else the drawn logo. */
    private static Image loadLogo(int size) {
        for (String name : new String[]{"logo.png", "logo.jpg", "logo.jpeg"}) {
            java.io.File f = new java.io.File(name);
            if (f.isFile()) {
                ImageIcon custom = new ImageIcon(f.getPath());
                if (custom.getIconWidth() > 0) {
                    // keep the picture's proportions, fit inside size x size
                    double scale = Math.min((double) size / custom.getIconWidth(),
                                            (double) size / custom.getIconHeight());
                    return custom.getImage().getScaledInstance(
                            Math.max(1, (int) (custom.getIconWidth() * scale)),
                            Math.max(1, (int) (custom.getIconHeight() * scale)), Image.SCALE_SMOOTH);
                }
            }
        }
        return drawLogo(size);
    }

    private static Icon logoIcon(int size) {
        return new ImageIcon(loadLogo(size));
    }

    /** A small colored circle with a letter, used as tab icons. */
    private static Icon badge(String letter, Color color) {
        BufferedImage img = new BufferedImage(18, 18, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.fillOval(0, 0, 17, 17);
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(letter, (18 - fm.stringWidth(letter)) / 2, 13);
        g.dispose();
        return new ImageIcon(img);
    }

    // ---------- Helpers ----------
    /** Library prints its lists to System.out; this captures that text for the GUI. */
    private static String capture(Runnable action) {
        PrintStream old = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true));
        try {
            action.run();
        } finally {
            System.setOut(old);
        }
        return buffer.toString().trim();
    }

    private static JTextArea newTextArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(MONO);
        area.setMargin(new Insets(8, 8, 8, 8));
        return area;
    }

    private static JPanel row(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        JLabel l = new JLabel(label);
        l.setPreferredSize(new Dimension(130, 24));
        p.add(l, BorderLayout.WEST);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Buklat-Aklat", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showScreen(JPanel panel, String name) {
        root.add(panel, name);
        cards.show(root, name);
    }

    private void seedData() {
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

    // ---------- Login screen ----------
    private JPanel buildLoginPanel() {
        JPanel outer = new JPanel(new GridBagLayout());

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        JLabel logo = new JLabel(logoIcon(120));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel title = new JLabel("BUKLAT-AKLAT");
        title.setFont(TITLE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel sub = new JLabel("Library Management System");
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginRole = new JComboBox<>(new String[]{"Student", "Librarian"});
        loginUser = new JTextField(18);
        loginPass = new JPasswordField(18);

        JButton loginBtn = new JButton("Log in");
        JButton signUpBtn = new JButton("Librarian sign up");
        loginBtn.addActionListener(e -> doLogin());
        loginPass.addActionListener(e -> doLogin());
        signUpBtn.addActionListener(e -> doSignUp());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        buttons.add(loginBtn);
        buttons.add(signUpBtn);

        box.add(logo);
        box.add(Box.createVerticalStrut(8));
        box.add(title);
        box.add(sub);
        box.add(Box.createVerticalStrut(20));
        box.add(row("Log in as", loginRole));
        box.add(Box.createVerticalStrut(8));
        box.add(row("Username", loginUser));
        box.add(Box.createVerticalStrut(8));
        box.add(row("Password", loginPass));
        box.add(Box.createVerticalStrut(16));
        box.add(buttons);

        outer.add(box);
        return outer;
    }

    private void doLogin() {
        String role = (String) loginRole.getSelectedItem();
        String username = loginUser.getText().trim();
        String password = new String(loginPass.getPassword()).trim();
        try {
            User user = library.login(role, username, password);
            loginPass.setText("");
            if (user instanceof Student) {
                showScreen(buildStudentPanel((Student) user), "student");
            } else {
                showScreen(buildLibrarianPanel((Librarian) user), "librarian");
            }
        } catch (LibraryException e) {
            showError(e.getMessage());
        }
    }

    private void doSignUp() {
        JTextField name = new JTextField(16);
        JTextField contact = new JTextField(16);
        JTextField username = new JTextField(16);
        JPasswordField password = new JPasswordField(16);
        JPanel form = new JPanel(new GridLayout(0, 1, 0, 6));
        form.add(row("Full name", name));
        form.add(row("Contact no.", contact));
        form.add(row("Username", username));
        form.add(row("Password", password));

        int result = JOptionPane.showConfirmDialog(this, form, "Librarian Sign Up",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        try {
            library.registerLibrarian(name.getText().trim(), contact.getText().trim(),
                    username.getText().trim(), new String(password.getPassword()).trim(), true);
            showInfo("Account created. You can now log in.");
        } catch (LibraryException e) {
            showError(e.getMessage());
        }
    }

    private void logout(User user) {
        capture(user::logout);
        cards.show(root, "login");
    }

    private JPanel header(User user, String role) {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(new Color(0x1B3A57));
        h.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel who = new JLabel(role + ": " + user.getName() + " (" + user.getUserID() + ")", logoIcon(36), SwingConstants.LEFT);
        who.setIconTextGap(10);
        who.setForeground(Color.WHITE);
        who.setFont(who.getFont().deriveFont(Font.BOLD, 15f));
        JButton out = new JButton("Logout");
        out.addActionListener(e -> logout(user));
        h.add(who, BorderLayout.WEST);
        h.add(out, BorderLayout.EAST);
        return h;
    }

    /** A text area that fills itself from a Library print method and has a Refresh button. */
    private JPanel listPanel(Runnable printer, JComponent bottom) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JTextArea area = newTextArea();
        Runnable refresh = () -> area.setText(capture(printer));
        refresh.run();
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refresh.run());
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        JPanel south = new JPanel(new BorderLayout(0, 6));
        JPanel refreshRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        refreshRow.add(refreshBtn);
        south.add(refreshRow, BorderLayout.NORTH);
        if (bottom != null) south.add(bottom, BorderLayout.CENTER);
        p.add(south, BorderLayout.SOUTH);
        p.putClientProperty("refresh", refresh);
        return p;
    }

    private static void refreshOf(JPanel listPanel) {
        ((Runnable) listPanel.getClientProperty("refresh")).run();
    }

    // ---------- Student screen ----------
    private JPanel buildStudentPanel(Student student) {
        JPanel screen = new JPanel(new BorderLayout());
        screen.add(header(student, "Student"), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        // Tab 1: book list + borrow
        JTextField borrowId = new JTextField(12);
        JButton borrowBtn = new JButton("Borrow");
        JPanel borrowRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        borrowRow.add(new JLabel("Book ID to borrow:"));
        borrowRow.add(borrowId);
        borrowRow.add(borrowBtn);
        JPanel booksTab = listPanel(library::printBookList, borrowRow);

        // Tab 2: my borrowed + return
        JTextField returnId = new JTextField(12);
        JButton returnBtn = new JButton("Return");
        JPanel returnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        returnRow.add(new JLabel("Book ID to return:"));
        returnRow.add(returnId);
        returnRow.add(returnBtn);
        JPanel myTab = listPanel(() -> library.printStudentBorrowed(student), returnRow);

        borrowBtn.addActionListener(e -> {
            try {
                BorrowRecord r = student.requestBorrow(library, borrowId.getText().trim());
                showInfo("Borrowed!\n" + r.getDueInfo());
                borrowId.setText("");
            } catch (LibraryException ex) {
                showError(ex.getMessage());
            }
            refreshOf(booksTab);
            refreshOf(myTab);
        });
        returnBtn.addActionListener(e -> {
            try {
                library.returnBook(student, returnId.getText().trim());
                showInfo("Book returned. Thank you!");
                returnId.setText("");
            } catch (LibraryException ex) {
                showError(ex.getMessage());
            }
            refreshOf(booksTab);
            refreshOf(myTab);
        });
        tabs.addChangeListener(e -> {
            refreshOf(booksTab);
            refreshOf(myTab);
        });

        tabs.addTab("Book List / Borrow", badge("B", new Color(0x2F5D8A)), booksTab);
        tabs.addTab("My Borrowed / Return", badge("M", new Color(0x76B041)), myTab);
        screen.add(tabs, BorderLayout.CENTER);
        return screen;
    }

    // ---------- Librarian screen ----------
    private JPanel buildLibrarianPanel(Librarian librarian) {
        JPanel screen = new JPanel(new BorderLayout());
        screen.add(header(librarian, "Librarian"), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        // Books: list + add + remove
        JTextField bookId = new JTextField(8);
        JTextField bookTitle = new JTextField(16);
        JTextField bookCat = new JTextField(10);
        JButton addBtn = new JButton("Add book");
        JTextField removeId = new JTextField(8);
        JButton removeBtn = new JButton("Remove book");

        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        addRow.add(new JLabel("ID:"));
        addRow.add(bookId);
        addRow.add(new JLabel("Title:"));
        addRow.add(bookTitle);
        addRow.add(new JLabel("Category:"));
        addRow.add(bookCat);
        addRow.add(addBtn);
        JPanel removeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        removeRow.add(new JLabel("Book ID to remove:"));
        removeRow.add(removeId);
        removeRow.add(removeBtn);
        JPanel bookControls = new JPanel(new GridLayout(0, 1, 0, 6));
        bookControls.add(addRow);
        bookControls.add(removeRow);
        JPanel booksTab = listPanel(library::printBookList, bookControls);

        addBtn.addActionListener(e -> {
            try {
                library.addBook(bookId.getText().trim(), bookTitle.getText().trim(), bookCat.getText().trim());
                bookId.setText("");
                bookTitle.setText("");
                bookCat.setText("");
                showInfo("Book added.");
            } catch (LibraryException ex) {
                showError(ex.getMessage());
            }
            refreshOf(booksTab);
        });
        removeBtn.addActionListener(e -> {
            try {
                library.removeBook(removeId.getText().trim());
                removeId.setText("");
                showInfo("Book removed.");
            } catch (LibraryException ex) {
                showError(ex.getMessage());
            }
            refreshOf(booksTab);
        });

        // Add student
        JPanel studentTab = buildAddStudentPanel();

        // Master list and borrow records
        JPanel masterTab = listPanel(() -> librarian.viewMasterList(library), null);
        JPanel recordsTab = listPanel(library::printBorrowRecords, null);

        // Confirm librarian accounts
        JPanel confirmTab = buildConfirmPanel(librarian);

        tabs.addTab("Books", badge("B", new Color(0x2F5D8A)), booksTab);
        tabs.addTab("Add Student", badge("S", new Color(0x76B041)), studentTab);
        tabs.addTab("Master List", badge("M", new Color(0xF3A712)), masterTab);
        tabs.addTab("Borrow Records", badge("R", new Color(0xE4572E)), recordsTab);
        tabs.addTab("Confirm Librarians", badge("C", new Color(0xD7263D)), confirmTab);
        tabs.addChangeListener(e -> {
            refreshOf(booksTab);
            refreshOf(masterTab);
            refreshOf(recordsTab);
            Runnable r = (Runnable) confirmTab.getClientProperty("refresh");
            r.run();
        });
        screen.add(tabs, BorderLayout.CENTER);
        return screen;
    }

    private JPanel buildAddStudentPanel() {
        JTextField name = new JTextField(20);
        JTextField id = new JTextField(20);
        JTextField contact = new JTextField(20);
        JTextField yp = new JTextField(20);
        JTextField username = new JTextField(20);
        JPasswordField password = new JPasswordField(20);
        JButton add = new JButton("Add student");

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 8));
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        form.add(row("Student name", name));
        form.add(row("Student ID", id));
        form.add(row("Contact no.", contact));
        form.add(row("Year level & program", yp));
        form.add(row("Login username", username));
        form.add(row("Password", password));
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.add(add);
        form.add(btnRow);

        add.addActionListener(e -> {
            try {
                library.addStudent(id.getText().trim(), name.getText().trim(), contact.getText().trim(),
                        yp.getText().trim(), username.getText().trim(), new String(password.getPassword()).trim());
                for (JTextField f : new JTextField[]{name, id, contact, yp, username, password}) f.setText("");
                showInfo("Student added.");
            } catch (LibraryException ex) {
                showError(ex.getMessage());
            }
        });

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(form, BorderLayout.NORTH);
        return wrap;
    }

    private JPanel buildConfirmPanel(Librarian librarian) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);
        list.setFont(MONO);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JButton confirm = new JButton("Confirm selected");

        Runnable refresh = () -> {
            model.clear();
            Librarian[] pending = library.getPendingLibrarians();
            for (Librarian l : pending) model.addElement(l.getName() + " (" + l.getUsername() + ")");
            if (pending.length == 0) model.addElement("No pending accounts.");
        };
        refresh.run();

        confirm.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            Librarian[] pending = library.getPendingLibrarians();
            if (idx < 0 || idx >= pending.length) {
                showError("Select a pending account first.");
                return;
            }
            try {
                librarian.confirmLibrarianAcct(pending[idx]);
                showInfo(pending[idx].getName() + " is now confirmed.");
            } catch (LibraryException ex) {
                showError(ex.getMessage());
            }
            refresh.run();
        });

        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        south.add(confirm);
        p.add(new JLabel("Pending librarian accounts:"), BorderLayout.NORTH);
        p.add(new JScrollPane(list), BorderLayout.CENTER);
        p.add(south, BorderLayout.SOUTH);
        p.putClientProperty("refresh", refresh);
        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryGUI().setVisible(true));
    }
}
