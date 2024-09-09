package maqlu.maqlulibrary.controllers;

import maqlu.maqlulibrary.entities.Book;
import maqlu.maqlulibrary.entities.User;
import maqlu.maqlulibrary.security.CurrentUserFinder;
import maqlu.maqlulibrary.services.BookService;
import maqlu.maqlulibrary.services.UserService;
import maqlu.maqlulibrary.utilities.DateTracker;
import maqlu.maqlulibrary.utilities.FineCalculator;
import maqlu.maqlulibrary.utilities.ListInStringConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService usService;

    @Autowired
    BookService bookService;

    @Autowired
    CurrentUserFinder currentUserFinder;

    @Autowired
    FineCalculator fineCalculator;

    @Autowired
    DateTracker dateTracker;

    @Autowired
    ListInStringConverter listConverter;

    private int maximumWeeksToExtend = 3;

    @GetMapping
    public String userHome(Model model) {
        User currentUser = currentUserFinder.getCurrentUser();
        model.addAttribute("booksWithFines", fineCalculator.selectBooksWithFines(currentUser.getBooks()));
        model.addAttribute("currentUser", currentUser);
        return "user/user-home.html";
    }

    @GetMapping(value="/yourbooks")
    @ResponseBody
    public ResponseEntity<List<Book>> yourBooks(Model model) {
        User currentUser = currentUserFinder.getCurrentUser();
        List<Book> books = currentUser.getBooks();
        LinkedHashMap<Book, BigDecimal> booksWithFines = fineCalculator.getBooksWithFines(books);
        model.addAttribute("books", booksWithFines);

        return ResponseEntity.ok(books);
    }

    @ResponseBody
    @GetMapping(value="/browsebooks")
    public ResponseEntity<List<Book>> browseBooks() {
        List<Book> books = bookService.findAll();
        return ResponseEntity.ok(books);
    }

    @PutMapping(value="/yourbooks/extend")
    @ResponseBody
    public String extendRequest(@RequestParam Long bookId,
                                @RequestParam int weeksToExtend,
                                Model model) {

        Book book = bookService.findById(bookId);

        book.setReturnDate(book.getReturnDate().plusDays(7 * weeksToExtend));
        bookService.save(book);

        return"redirect:/user/yourbooks/bookextended";
    }

    @GetMapping(value="/yourbooks/bookextended")
    @ResponseBody
    public ResponseEntity<String> bookExtended() {
        return ResponseEntity.ok("Wypozyczenie przedluzone");
    }

    @GetMapping(value="/yourreservations")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> yourReservations() {
        User currentUser = currentUserFinder.getCurrentUser();
        List<Book> reservedBooks = currentUser.getReservedBooks();

        Map<String, Object> response = new HashMap<>();
        response.put("reservedBooks", reservedBooks);

        return ResponseEntity.ok(response);
    }
}
