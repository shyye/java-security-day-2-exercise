package com.booleanuk.api.controllers;

import com.booleanuk.api.models.Item;
import com.booleanuk.api.models.Loan;
import com.booleanuk.api.models.User;
import com.booleanuk.api.payload.responses.*;
import com.booleanuk.api.repositories.ItemRepository;
import com.booleanuk.api.repositories.LoanRepository;
import com.booleanuk.api.repositories.UserRepository;
import com.booleanuk.api.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("loans")
public class LoanController {
    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    // FOR ADMIN
    // Get all loans and see corresponding user/item
    @GetMapping
    public ResponseEntity<LoanListResponse> getAll() {
        LoanListResponse response = new LoanListResponse();
        response.set(this.loanRepository.findAll());
        return ResponseEntity.ok(response);
    }

    // FOR USERS
    // Get current loans
    @GetMapping("myLoans")
    public ResponseEntity<LoanListResponse> getMyLoans() {
        LoanListResponse response = new LoanListResponse();
        response.set(this.loanRepository.findAllByStatus("current"));
        return ResponseEntity.ok(response);
    }

    // Get history of previous loans
    @GetMapping("myHistory")
    public ResponseEntity<LoanListResponse> getMyHistory() {
        LoanListResponse response = new LoanListResponse();
        response.set(this.loanRepository.findAllByStatus("archived"));
        return ResponseEntity.ok(response);
    }

    // Borrow an item
    @PostMapping("create")
    public ResponseEntity<Response<?>> create(@RequestBody Loan loan) {
        LoanResponse response = new LoanResponse();

        // Set status
        loan.setStatus("current");

        // Get logged in user
        User user = getLoggedInUser();
        if (user == null) {
            ErrorResponse error = new ErrorResponse();
            error.set("User not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        // Get Item
        Item item = itemRepository.findById(loan.getItem().getId()).orElse(null);
        if (item == null) {
            ErrorResponse error = new ErrorResponse();
            error.set("Item not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        // Check if item is already borrowed
        if (item.getIsBorrowed()) {
            ErrorResponse error = new ErrorResponse();
            error.set("Item already borrowed");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        try {
            item.setIsBorrowed(true);
            loan.setUser(user);
            loan.setItem(item);
            response.set(this.loanRepository.save(loan));
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse();
            error.set("Bad request");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Return item
    @PutMapping("/return/{id}")
    public ResponseEntity<Response<?>> returnItem(@PathVariable int id) {
        Loan loan = this.loanRepository.findById(id).orElse(null);
        if (loan == null) {
            ErrorResponse error = new ErrorResponse();
            error.set("Not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        Item borrowedItem = this.itemRepository.findById(loan.getItem().getId()).orElse(null);
        // Return item
        if (borrowedItem == null) {
            ErrorResponse error = new ErrorResponse();
            error.set("Item not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        borrowedItem.setIsBorrowed(false);
        this.itemRepository.save(borrowedItem);

        // Update loan status
        loan.setStatus("archived");
        this.loanRepository.save(loan);

        LoanResponse response = new LoanResponse();
        response.set(loan);
        return ResponseEntity.ok(response);
    }

    // Get the logged in user
    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetailsImpl.getUsername();
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);
    }
}
