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
    public ResponseEntity<Map<String, Object>> yourBooks() {
        User currentUser = currentUserFinder.getCurrentUser();
        List<Book> books = currentUser.getBooks();

        Map<String, Object> response = new HashMap<>();
        response.put("currentUser", currentUser);
        response.put("booksWithFines", fineCalculator.getBooksWithFines(books));
        response.put("booksWithoutFine",currentUser.getBooks());

        return ResponseEntity.ok(response);
    }

    @PutMapping(value="/yourbooks/extend")
    public String extendRequest(@RequestParam BigDecimal fineAmount,
                                @RequestParam Long bookId,
                                @RequestParam int weeksToExtend,
                                Model model) {

        Book book = bookService.findById(bookId);
        int extensionsLeft = maximumWeeksToExtend - book.getTimesExtended();
        long daysTooLate = dateTracker.daysTooLate(book.getReturnDate());

        if (book.getTimesExtended() < maximumWeeksToExtend && fineAmount.compareTo(BigDecimal.valueOf(0)) == 0 && book.getReservedByUser() == null) {
            book.setReturnDate(book.getReturnDate().plusDays(7 * weeksToExtend));
            book.setTimesExtended(book.getTimesExtended() + weeksToExtend);
            bookService.save(book);
            return"redirect:/user/yourbooks/bookextended";

        } else if (fineAmount.compareTo(BigDecimal.valueOf(0)) == 1 && daysTooLate <= (extensionsLeft * 7) && book.getReservedByUser() == null) {
            return "redirect:/user/yourbooks/payfine/" + bookId;

        } else {
            return "redirect:/user/yourbooks/bookcannotbeextended";

        }
    }

    @GetMapping(value="/yourbooks/bookextended")
    public String bookExtended() {
        return "user/user-book-extended.html";
    }

    @GetMapping(value="/yourbooks/bookcannotbeextended")
    public String bookCanNotBeExtended() {
        return "user/user-book-can-not-be-extended.html";
    }

    @GetMapping(value="/browsebooks")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> browseBooksJson(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String showAllBooks,
            @RequestParam(required = false) Long reservedBookId,
            @RequestParam(required = false) Long removeBookId,
            @RequestParam(required = false) String reservedBookIdsInString) {

        Set<Long> reservedBookIds = new LinkedHashSet<>();
        if (reservedBookIdsInString != null) {
            reservedBookIds = listConverter.convertListInStringToSetInLong(reservedBookIdsInString);
        }
        if (removeBookId != null) {
            reservedBookIds.remove(removeBookId);
        }
        if (reservedBookId != null) {
            reservedBookIds.add(reservedBookId);
        }

        Map<Book, String> listedBookReservations = dateTracker.listedBookReservations(reservedBookIds);

        List<Book> books;
        if (showAllBooks == null) {
            books = bookService.searchBooks(title, author);
        } else {
            books = bookService.findAll();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("userHasFine", fineCalculator.hasFineOrNot(currentUserFinder.getCurrentUser()));
        response.put("listedBookReservations", listedBookReservations);
        response.put("reservedBookIds", reservedBookIds);
        response.put("title", title);
        response.put("author", author);
        response.put("showAllBooks", showAllBooks);
        response.put("books", books);

        return ResponseEntity.ok(response);
    }

    @GetMapping(value="/FAQ")
    public String FAQ() {
        return "user/user-FAQ.html";
    }


    @PutMapping(value="/browsebooks/payreservation")
    public String payReservation(@RequestParam String reservedBookIdsInString,
                                 @RequestParam Double amountToPay,
                                 Model model) {

        model.addAttribute("amountToPay", amountToPay);
        model.addAttribute("reservedBookIdsInString", reservedBookIdsInString);
        return "user/user-pay-reservation.html";
    }

    @PutMapping(value="browsebooks/savereservation")
    public String saveBookReservations(@RequestParam String reservedBookIdsInString) {
        Set<Long> reservedBookIds = listConverter.convertListInStringToSetInLong(reservedBookIdsInString);
        dateTracker.setReserervationDatesAndReservedByCurrentUserForMultipleBooks(reservedBookIds);
        return "redirect:/user/yourreservations";
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
