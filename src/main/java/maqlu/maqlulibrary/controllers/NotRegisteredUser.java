package maqlu.maqlulibrary.controllers;

import maqlu.maqlulibrary.entities.Book;
import maqlu.maqlulibrary.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/notregistered")
public class NotRegisteredUser {

    @Autowired
    BookService bookService;

    @ResponseBody
    @GetMapping(value="/browsebooks")
    public ResponseEntity<List<Book>> browseBooks() {
        List<Book> books = bookService.findAll();
        return ResponseEntity.ok(books);
    }
}
