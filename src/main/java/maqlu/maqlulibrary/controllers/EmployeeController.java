
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
    public ResponseEntity<List<User>> showUsers() {
        List<User> users = usService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping(value = "/books/showbooks")
    @ResponseBody
    public ResponseEntity<List<Book>> showBooks() {
        List<Book> books = bookService.findAll();
        return ResponseEntity.ok(books);
    }

    @GetMapping(value = "/users/showuserinfo")
    @ResponseBody
    public ResponseEntity<User> showUserInfo(@RequestParam Long userId) {
        User user = usService.findById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping(value="/books/newbook")
    @ResponseBody
    public ResponseEntity<String> newBook(Model model) {
        model.addAttribute("book", new Book());
        return ResponseEntity.ok("tu trzeba jakis formularz zwrocic zeby ksiazke nowa dodac");
    }

    @PostMapping(value="/books/save")
    public String saveBook(Book book) {
        bookService.save(book);
        return "redirect:/employee/books/booksaved";
    }

    @GetMapping(value="/books/booksaved")
    @ResponseBody
    public ResponseEntity<String> bookSaved() {
        return ResponseEntity.ok("Dodano ksiazke");
    }

    @DeleteMapping(value="/books/deletebook")
    public String deleteBook(@RequestParam Long deleteBookId) {
        bookService.deleteById(deleteBookId);
        return "redirect:/employee/books/bookdeleted";
    }

    @GetMapping(value="/books/bookdeleted")
    @ResponseBody
    public ResponseEntity<String> bookDeleted() {
        return ResponseEntity.ok("Usunieto ksiazke");
    }

    @GetMapping(value="/books/changebookinfo")
    @ResponseBody
    public ResponseEntity<Book> changeBookInfo(@RequestParam Long changeBookId, Model model) {
        Book book = bookService.findById(changeBookId);
        model.addAttribute("book", book);
        return ResponseEntity.ok(book);
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
    @ResponseBody
    public ResponseEntity<String> bookInfoChanged() {
        return ResponseEntity.ok("Zapisano zmiany");
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

    @GetMapping(value="/reservations")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> reservations(Model model) {
        Map<String, Object> response = new HashMap<>();

        response.put("unprocessedReservations", bookService.getUnprocessedBookReservations());
        response.put("processedReservations", bookService.getProcessedBookReservations());
        return ResponseEntity.ok(response);
    }

    @PutMapping(value="/setreadyforpickup")
    @ResponseBody
    public ResponseEntity<String> setReadyForPickup(@RequestParam Long bookId,
                                    @RequestParam Long userId,
                                    Model model) {
        model.addAttribute("user", usService.findById(userId));
        model.addAttribute("book", bookService.findById(bookId));
        return ResponseEntity.ok("Ksiazka gotowa do odbioru");
    }
}
