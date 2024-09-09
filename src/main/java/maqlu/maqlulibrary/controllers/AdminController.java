package maqlu.maqlulibrary.controllers;

import maqlu.maqlulibrary.entities.User;
import maqlu.maqlulibrary.security.CurrentUserFinder;
import maqlu.maqlulibrary.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {
    @Autowired
    UserService usService;

    @Autowired
    CurrentUserFinder currentUserFinder;

    @GetMapping
    public String adminHome(Model model) {
        User currentUser = currentUserFinder.getCurrentUser();
        model.addAttribute("currentUser", currentUser);
        return "admin/admin-home.html";
    }

    @GetMapping(value = "/manageaccounts")
    @ResponseBody
    public ResponseEntity<List<User>> manageAuthorities() {
        List<User> users = usService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping(value = "/manageaccount")
    @ResponseBody
    public User manageAccount(@RequestParam Long userId) {
        User user = usService.findById(userId);
        return user;
    }

    @PutMapping(value="/saveaccountsettings")
    public String saveAccountSettings(@RequestParam boolean accStatus,
                                      @RequestParam String role,
                                      @RequestParam Long userId) {
        User user = usService.findById(userId);
        user.setRole(role);
        user.setEnabled(accStatus);
        usService.save(user);
        return "redirect:/admin/accountsettingssaved";
    }

    @GetMapping(value="/accountsettingssaved")
    @ResponseBody
    public ResponseEntity<String> accountSettingsSaved() {
        return ResponseEntity.ok("Konto zaktualizowane");
    }
}