package maqlu.maqlulibrary.utilities;

import maqlu.maqlulibrary.entities.*;
import maqlu.maqlulibrary.services.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MidnightApplicationRefresh {
    @Autowired
    BookService bookService;

    @Autowired
    UserService usService;

    @Autowired
    NotificationService notifService;

    //Removes overdue reservations and notifications.
    public void midnightApplicationRefresher() {

        for (Book book : bookService.findAll()) {
            if (book.getEndReservationDate() != null) {
                if (book.getEndReservationDate().compareTo(LocalDate.now()) == -1) {
                    if (book.getReservedByUser() != null) {
                        User user = book.getReservedByUser();
                        book.setReservedByUser(null);
                        usService.save(user);
                    }
                    book.setStartReservationDate(null);
                    book.setEndReservationDate(null);
                    book.setReadyForPickup(false);
                    bookService.save(book);
                }
            }
        }

        for (Notification notif : notifService.findAll()) {
            if (notif.getValidUntilDate() != null) {
                if (notif.getValidUntilDate().compareTo(LocalDate.now()) == -1) {
                    notifService.deleteById(notif.getNotificationId());
                }
            }
        }
    }
}
