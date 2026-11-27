## BUKLAT-AKLAT

Java Console Library Management System | OOP 101

A simple library system for viewing books, managing student accounts, and tracking book loans and returns. Built using Java classes, inheritance, and fixed-size arrays.

### 1. Overview

Buklat-Aklat brings book availability, borrower information, and due dates into one console application. It is designed for students who borrow books and librarians who maintain the library records. The project addresses the difficulty of keeping manual or separate transaction lists consistent.

### 2. Objectives and main features
- Students: log in, view the book list, borrow available books, check active loans and due dates, and return their own borrowed books.
- Librarians: log in, add/remove books, register students, view master lists and borrowing records, and access the account-confirmation menu.
- Rules: seven-day loans, up to three active loans per student, and checks against duplicate IDs, unavailable books, and invalid returns.

### 3. OOP concepts demonstrated

| Concept | Application in Buklat-Aklat |
| --- | --- |
| Encapsulation | Book, User, Student, Librarian, BorrowRecord, and Library keep their fields private and expose behavior through methods. |
| Abstraction | User is abstract and declares displayMenu() and getRole(). Library hides lookup and loan-rule details behind operations such as borrowBook(). |
| Inheritance | Student and Librarian extend User, reusing common identity, login(), and logout() behavior. |
| Polymorphism | Both user subclasses override displayMenu() and getRole(). Library.login() returns User; Main selects a role-specific session with instanceof. |
| Exception handling | LibraryException is a custom checked exception. Main catches rule violations and displays readable error messages. |

Implementation note: no custom interface is defined. Menu.java is a standalone greeting class; Main.java runs the full application.

## PROJECT SETUP

Folder structure, requirements, and run instructions

### 4. Project structure

```text
Buklat-Aklat/
|-- src/
|   |-- Main.java              # Entry point and console flows
|   |-- Library.java           # Storage and borrowing rules
|   |-- User.java              # Abstract base class
|   |-- Student.java           # Student behavior
|   |-- Librarian.java         # Librarian behavior
|   |-- Book.java              # Catalog item
|   |-- BorrowRecord.java      # Loan dates and status
|   |-- LibraryException.java  # Business-rule errors
|   `-- Menu.java              # Greeting only
|-- docs/                      # Documentation and UML
|-- screenshots/               # Program output images
`-- README.md
```

### 5. Requirements and technologies
- Java Development Kit: JDK 11 or later recommended; compilation and execution verified using OpenJDK 11.
- Terminal or a Java IDE. No external application libraries are required.
- Java standard APIs: Scanner for input; LocalDate, DateTimeFormatter, and ChronoUnit for date handling. Data is stored in arrays, not a database.

### 6. How to run the program

Open a terminal in the project root, then compile and run:

```text
mkdir out
javac -d out src/*.java
java -cp out Main
```

Skip mkdir if out exists. On Windows Command Prompt, use src\*.java if needed. In an IDE, run Main.main(), not Menu.main().

| Demo role | Username / password |
| --- | --- |
| Librarian | admin / admin123 |
| Student | juan / pass123 |

To try it: log in as juan, borrow B-001, view due information, and return it. Log out, then log in as admin to inspect the records. All changes are lost when the program exits.

## SYSTEM DESIGN

UML class diagram

### 7. UML class diagram

![Core classes and selected members. Full-resolution diagram is included separately.](UML-Diagram.png)

Student and Librarian inherit from User. Library stores arrays of books, students, librarians, and loan records. Student and Librarian delegate operations to Library. BorrowRecord links to Book and Student using string IDs, not object fields.

Supporting classes: Main controls the console workflow; LibraryException extends Exception; Menu is a separate greeting-only entry point. The field BorrowRecord.returnDate represents the due date, not an actual return timestamp.

## PROJECT NOTES

Output example, limitations, and team information

### 8. Sample program output


### 9. Known limitations

Data is session-only. Capacities are 100 books, 50 students, 10 librarians, and 500 cumulative loan records. Passwords are plaintext. There is no GUI, search menu, fine calculation, renewal, reservation, or actual-return timestamp.

Important: public librarian signup currently passes confirmed=true, so new accounts can log in immediately. Confirmation support exists, but normal signup does not create pending accounts. “Request to borrow” also creates a loan immediately; there is no loan approval queue.

### 10. Future improvements

Correct the signup approval flow; hash passwords and strengthen authorization; add persistent storage, stronger validation, catalog search/edit, actual-return dates, and a graphical or web interface.

### 11. Team members and contributors

| Name / username | Role and actual contribution |
| --- | --- |
| De Juan. Eric Harvey | [Role; specific files/features; real commit or PR link] |
| Bicua, Shawm Anthiny | [Role; specific files/features; real commit or PR link] |
| Enojo, Aerhic Tyrone B.  | [Role; specific files/features; real commit or PR link] |

Section: [Section] | Instructor: [Name] | Date: [Date]
Repository: [Actual URL]. 


