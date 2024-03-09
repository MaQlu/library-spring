package maqlu.maqlulibrary.controllers;

import maqlu.maqlulibrary.entities.Book;
import maqlu.maqlulibrary.entities.Notification;
import maqlu.maqlulibrary.entities.User;
import maqlu.maqlulibrary.security.CurrentUserFinder;
import maqlu.maqlulibrary.services.BookService;
import maqlu.maqlulibrary.services.NotificationService;
import maqlu.maqlulibrary.services.UserService;
import maqlu.maqlulibrary.utilities.FineCalculator;
import maqlu.maqlulibrary.utilities.ListInStringConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
@Controller
@RequestMapping(value = "/employee")
public class EmployeeController {
    @Autowired
    UserService usService;

    @Autowired
    BookService bookService;

    @Autowired
    CurrentUserFinder currentUserFinder;

    @Autowired
    NotificationService notifService;

    @Autowired
    FineCalculator fineCalculator;

    @Autowired
    ListInStringConverter listConverter;

    @GetMapping
    public String employeeHomePage(Model model) {
        long currentUserId = currentUserFinder.getCurrentUserId();
        User currentUser = usService.findById(currentUserId);
        model.addAttribute("currentUser", currentUser);
        return"employee/employee-home.html";
    }

    @GetMapping(value="/users/showusers")
    @ResponseBody
    public ResponseEntity<List<User>> showUsers(Model model,
                            @RequestParam(required=false)String firstName,
                            @RequestParam (required=false)String lastName,
                            @RequestParam (required=false)String showAllUsers) {

        List<User> users = new ArrayList<User>();

        if (showAllUsers != null) users = usService.findAll();
        else if (firstName != null || lastName != null) users = usService.userSearcher(firstName, lastName);

        return ResponseEntity.ok(users);
    }

    @GetMapping(value = "/users/showuserinfo")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> showUserInfo(@RequestParam Long userId,
                                                            @RequestParam BigDecimal fine,
                                                            Model model) {

        User user = usService.findById(userId);
        // Create a Map to hold user details and books with fines
        Map<String, Object> userData = new HashMap<>();
        userData.put("user", user);
        userData.put("booksInUse",fineCalculator.getBooksWithFines(user.getBooks()));
        userData.put("fine", fine);

        return ResponseEntity.ok(userData);
    }

    @GetMapping(value="/books")
    public String books() {
        return "employee/employee-books.html";
    }

    @GetMapping(value = "/books/showbooks")
    @ResponseBody
    public ResponseEntity<List<Book>> showBooks(@RequestParam(required = false) String title,
                                                @RequestParam(required = false) String author,
                                                @RequestParam(required = false) String showAllBooks) {

        List<Book> books;
        if (showAllBooks == null) {
            books = bookService.searchBooks(title, author);
        } else {
            books = bookService.findAll();
        }

        return ResponseEntity.ok(books);
    }

    @GetMapping(value="/books/newbook")
    public String newBook(Model model) {
        model.addAttribute("book", new Book());
        return "employee/employee-new-book.html";
    }

    @PostMapping(value="/books/save")
    public String saveBook(Book book) {
        bookService.save(book);
        return "redirect:/employee/books/booksaved";
    }

    @GetMapping(value="/books/booksaved")
    public String bookSaved() {
        return "employee/employee-book-saved.html";
    }

    @GetMapping(value="/books/areyousuretodeletebook")
    public String areYouSureToDeleteBook(@RequestParam Long deleteBookId, Model model) {
        Book book = bookService.findById(deleteBookId);
        model.addAttribute("deleteBook", book);
        return "employee/employee-delete-book.html";
    }

    @DeleteMapping(value="/books/deletebook")
    public String deleteBook(@RequestParam Long deleteBookId) {
        bookService.deleteById(deleteBookId);
        return "redirect:/employee/books/bookdeleted";
    }

    @GetMapping(value="/books/bookdeleted")
    public String bookDeleted() {
        return "employee/employee-book-deleted.html";
    }

    @GetMapping(value="/books/changebookinfo")
    public String changeBookInfo(@RequestParam Long changeBookId, Model model) {
        Book book = bookService.findById(changeBookId);
        model.addAttribute("book", book);
        return "employee/employee-change-book-info.html";
    }

    @PutMapping(value="/books/savebookchange")
    public String updatebookinfo(@RequestParam (required=false) String removeCurrentUser,
                                 @RequestParam (required=false) String removeReservation,
                                 Book book) {
        if (removeCurrentUser != null) bookService.removeCurrentUserOfBook(book);
        if (removeReservation != null) bookService.removeReservation(book);
        bookService.save(book);
        return "redirect:/employee/books/bookinfochanged";
    }

    @GetMapping(value="/books/bookinfochanged")
    public String bookInfoChanged() {
        return "employee/employee-book-information-changed.html";
    }

    @GetMapping(value = "/returnedbooks")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> returnedBooksJson(@RequestParam(required = false) Long userId,
                                                                 @RequestParam(required = false) String firstName,
                                                                 @RequestParam(required = false) String lastName,
                                                                 @RequestParam(required = false) Long selectedBookId,
                                                                 @RequestParam(required = false) Long removeBookId,
                                                                 @RequestParam(required = false) String selectedBookIdsInString,
                                                                 Model model) {

        List<User> users = usService.userSearcher(firstName, lastName);

        User user = null;
        if (userId != null) {
            user = usService.findById(userId);
        }

        LinkedHashMap<Book, BigDecimal> booksInUseByUser = null;
        if (user != null) {
            booksInUseByUser = fineCalculator.getBooksWithFines(user.getBooks());
        }

        Set<Long> selectedBookIds = new LinkedHashSet<>();
        if (selectedBookIdsInString != null) {
            selectedBookIds = listConverter.convertListInStringToSetInLong(selectedBookIdsInString);
        }
        if (removeBookId != null) {
            selectedBookIds.remove(removeBookId);
        }
        if (selectedBookId != null) {
            selectedBookIds.add(selectedBookId);
        }

        LinkedHashMap<Book, BigDecimal> selectedBooks = fineCalculator.getBooksWithFines(bookService.convertIdsCollectionToBooksList(selectedBookIds));
        BigDecimal fineToPay = fineCalculator.getTotalFine(bookService.convertIdsCollectionToBooksList(selectedBookIds));

        Map<String, Object> response = new HashMap<>();
        response.put("users", users);
        response.put("user", user);
        response.put("booksInUseByUser", booksInUseByUser);
        response.put("selectedBookIds", selectedBookIds);
        response.put("selectedBooks", selectedBooks);
        response.put("fineToPay", fineToPay);

        return ResponseEntity.ok(response);
    }


    //to delete
    @GetMapping(value="/confirmreturnedbooks")
    public String confirmReturnedBooks(@RequestParam Long userId,
                                       @RequestParam BigDecimal fineToPay,
                                       @RequestParam String selectedBookIdsInString,
                                       Model model) {
        Set<Long> selectedBookIds = listConverter.convertListInStringToSetInLong(selectedBookIdsInString);
        List<Book> selectedBooks = bookService.convertIdsCollectionToBooksList(selectedBookIds);

        model.addAttribute("selectedBooks", selectedBooks);
        model.addAttribute("selectedBookIds", selectedBookIds);
        model.addAttribute("user", usService.findById(userId));
        model.addAttribute("fineToPay", fineToPay);
        return "employee/employee-confirm-returned-books.html";
    }


    @PutMapping(value="/savereturnedbooks")
    public String saveReturnedBooks(@RequestParam String selectedBookIdsInString) {
        Set<Long> returnedBooks = listConverter.convertListInStringToSetInLong(selectedBookIdsInString);
        List<Book> books = bookService.convertIdsCollectionToBooksList(returnedBooks);
        bookService.removeCurrentUserOfMultipleBooks(books);
        return "redirect:/employee/booksreturned";
    }

    //to delete
    @GetMapping(value="/booksreturned")
    public String booksReturned() {
        return "employee/employee-books-returned.html";
    }

    @GetMapping(value="/reservations")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> reservations(Model model) {
        Map<String, Object> response = new HashMap<>();

        response.put("unprocessedReservations", bookService.getUnprocessedBookReservations());
        response.put("processedReservations", bookService.getProcessedBookReservations());
        return ResponseEntity.ok(response);
    }

    @PutMapping(value="/setreadyforpickup")
    public String setReadyForPickup(@RequestParam Long bookId,
                                    @RequestParam Long userId,
                                    Model model) {
        model.addAttribute("user", usService.findById(userId));
        model.addAttribute("book", bookService.findById(bookId));
        return "employee/employee-reservation-ready-for-pick-up.html";
    }

    @PutMapping(value="/updatebookreservation")
    public String updateBookReservation(@RequestParam Long bookId,
                                        @RequestParam Long userId) {

        Book book = bookService.findById(bookId);
        Notification notification = new Notification(LocalDate.now(), book.getEndReservationDate(), "Ksiazke mozesz odebrac do: " +
                book.getEndReservationDate() + ". " + book.getTitle() + " by " + book.getAuthor() + ".");

        notification.setValidUntilDate(book.getEndReservationDate());
        notification.setNotificationReceiver(usService.findById(userId));
        notifService.save(notification);
        usService.saveById(userId);
        book.setReadyForPickup(true);
        bookService.save(book);
        return "redirect:/employee/reservations";
    }
}
