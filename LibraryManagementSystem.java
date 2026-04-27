import java.util.*;
import java.time.LocalDate;

// Book class
class Book {
    private String isbn;
    private String title;
    private String author;
    private boolean isAvailable;
    
    public Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.isAvailable = true;
    }
    
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    
    @Override
    public String toString() {
        return String.format("ISBN: %s | Title: %s | Author: %s | Status: %s", 
            isbn, title, author, isAvailable ? "Available" : "Borrowed");
    }
}

// Member class
class Member {
    private String memberId;
    private String name;
    private String email;
    private List<BorrowRecord> borrowedBooks;
    
    public Member(String memberId, String name, String email) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.borrowedBooks = new ArrayList<>();
    }
    
    public String getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<BorrowRecord> getBorrowedBooks() { return borrowedBooks; }
    
    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | Email: %s | Books Borrowed: %d", 
            memberId, name, email, borrowedBooks.size());
    }
}

// BorrowRecord class
class BorrowRecord {
    private Book book;
    private Member member;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    
    public BorrowRecord(Book book, Member member) {
        this.book = book;
        this.member = member;
        this.borrowDate = LocalDate.now();
        this.dueDate = borrowDate.plusDays(14); // 2 weeks loan period
    }
    
    public Book getBook() { return book; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    
    public boolean isOverdue() {
        return returnDate == null && LocalDate.now().isAfter(dueDate);
    }
    
    @Override
    public String toString() {
        String status = returnDate != null ? "Returned on " + returnDate : 
                       (isOverdue() ? "OVERDUE" : "Due: " + dueDate);
        return String.format("%s | Borrowed: %s | %s", 
            book.getTitle(), borrowDate, status);
    }
}

// Library class
class Library {
    private Map<String, Book> books;
    private Map<String, Member> members;
    private List<BorrowRecord> borrowHistory;
    
    public Library() {
        books = new HashMap<>();
        members = new HashMap<>();
        borrowHistory = new ArrayList<>();
    }
    
    // Book operations
    public void addBook(Book book) {
        books.put(book.getIsbn(), book);
        System.out.println("Book added successfully: " + book.getTitle());
    }
    
    public void removeBook(String isbn) {
        Book book = books.remove(isbn);
        if (book != null) {
            System.out.println("Book removed: " + book.getTitle());
        } else {
            System.out.println("Book not found!");
        }
    }
    
    public void displayAllBooks() {
        if (books.isEmpty()) {
            System.out.println("No books in library.");
            return;
        }
        System.out.println("\n=== All Books ===");
        books.values().forEach(System.out::println);
    }
    
    public void searchBooksByTitle(String title) {
        System.out.println("\n=== Search Results ===");
        books.values().stream()
            .filter(b -> b.getTitle().toLowerCase().contains(title.toLowerCase()))
            .forEach(System.out::println);
    }
    
    // Member operations
    public void addMember(Member member) {
        members.put(member.getMemberId(), member);
        System.out.println("Member registered: " + member.getName());
    }
    
    public void displayAllMembers() {
        if (members.isEmpty()) {
            System.out.println("No members registered.");
            return;
        }
        System.out.println("\n=== All Members ===");
        members.values().forEach(System.out::println);
    }
    
    // Borrow and return operations
    public void borrowBook(String memberId, String isbn) {
        Member member = members.get(memberId);
        Book book = books.get(isbn);
        
        if (member == null) {
            System.out.println("Member not found!");
            return;
        }
        if (book == null) {
            System.out.println("Book not found!");
            return;
        }
        if (!book.isAvailable()) {
            System.out.println("Book is already borrowed!");
            return;
        }
        
        BorrowRecord record = new BorrowRecord(book, member);
        member.getBorrowedBooks().add(record);
        borrowHistory.add(record);
        book.setAvailable(false);
        
        System.out.println(member.getName() + " borrowed \"" + book.getTitle() + "\"");
        System.out.println("Due date: " + record.getDueDate());
    }
    
    public void returnBook(String memberId, String isbn) {
        Member member = members.get(memberId);
        Book book = books.get(isbn);
        
        if (member == null || book == null) {
            System.out.println("Invalid member or book!");
            return;
        }
        
        BorrowRecord record = member.getBorrowedBooks().stream()
            .filter(r -> r.getBook().getIsbn().equals(isbn) && r.getReturnDate() == null)
            .findFirst()
            .orElse(null);
        
        if (record == null) {
            System.out.println("This book was not borrowed by this member!");
            return;
        }
        
        record.setReturnDate(LocalDate.now());
        member.getBorrowedBooks().remove(record);
        book.setAvailable(true);
        
        System.out.println("Book returned: " + book.getTitle());
        if (record.isOverdue()) {
            System.out.println("WARNING: Book was overdue!");
        }
    }
    
    public void displayMemberHistory(String memberId) {
        Member member = members.get(memberId);
        if (member == null) {
            System.out.println("Member not found!");
            return;
        }
        
        System.out.println("\n=== Borrow History for " + member.getName() + " ===");
        System.out.println("Currently borrowed:");
        member.getBorrowedBooks().forEach(System.out::println);
        
        System.out.println("\nPast borrows:");
        borrowHistory.stream()
            .filter(r -> r.getBook() != null && r.getReturnDate() != null)
            .filter(r -> borrowHistory.contains(r))
            .forEach(System.out::println);
    }
}

// Main class
public class LibraryManagementSystem {
    public static void main(String[] args) {
        Library library = new Library();
        Scanner scanner = new Scanner(System.in);
        
        // Sample data
        library.addBook(new Book("001", "Java Programming", "James Gosling"));
        library.addBook(new Book("002", "Clean Code", "Robert Martin"));
        library.addBook(new Book("003", "Design Patterns", "Gang of Four"));
        
        library.addMember(new Member("M001", "Alice Johnson", "alice@email.com"));
        library.addMember(new Member("M002", "Bob Smith", "bob@email.com"));
        
        while (true) {
            System.out.println("\n=== Library Management System ===");
            System.out.println("1. Add Book");
            System.out.println("2. Display All Books");
            System.out.println("3. Search Books");
            System.out.println("4. Add Member");
            System.out.println("5. Display All Members");
            System.out.println("6. Borrow Book");
            System.out.println("7. Return Book");
            System.out.println("8. View Member History");
            System.out.println("9. Exit");
            System.out.print("Choose option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    System.out.print("Enter ISBN: ");
                    String isbn = scanner.nextLine();
                    System.out.print("Enter Title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter Author: ");
                    String author = scanner.nextLine();
                    library.addBook(new Book(isbn, title, author));
                    break;
                    
                case 2:
                    library.displayAllBooks();
                    break;
                    
                case 3:
                    System.out.print("Enter title to search: ");
                    String searchTitle = scanner.nextLine();
                    library.searchBooksByTitle(searchTitle);
                    break;
                    
                case 4:
                    System.out.print("Enter Member ID: ");
                    String memberId = scanner.nextLine();
                    System.out.print("Enter Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Email: ");
                    String email = scanner.nextLine();
                    library.addMember(new Member(memberId, name, email));
                    break;
                    
                case 5:
                    library.displayAllMembers();
                    break;
                    
                case 6:
                    System.out.print("Enter Member ID: ");
                    String borrowMemberId = scanner.nextLine();
                    System.out.print("Enter Book ISBN: ");
                    String borrowIsbn = scanner.nextLine();
                    library.borrowBook(borrowMemberId, borrowIsbn);
                    break;
                    
                case 7:
                    System.out.print("Enter Member ID: ");
                    String returnMemberId = scanner.nextLine();
                    System.out.print("Enter Book ISBN: ");
                    String returnIsbn = scanner.nextLine();
                    library.returnBook(returnMemberId, returnIsbn);
                    break;
                    
                case 8:
                    System.out.print("Enter Member ID: ");
                    String historyMemberId = scanner.nextLine();
                    library.displayMemberHistory(historyMemberId);
                    break;
                    
                case 9:
                    System.out.println("Thank you for using Library Management System!");
                    scanner.close();
                    return;
                    
                default:
                    System.out.println("Invalid option!");
            }
        }
    }
}