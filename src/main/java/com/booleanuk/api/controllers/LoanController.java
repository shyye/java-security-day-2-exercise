package com.booleanuk.api.controllers;

import com.booleanuk.api.models.Item;
import com.booleanuk.api.models.Loan;
import com.booleanuk.api.models.User;
import com.booleanuk.api.payload.responses.*;
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

    @GetMapping
    public ResponseEntity<LoanListResponse> getAll() {
        LoanListResponse response = new LoanListResponse();
        response.set(this.loanRepository.findAll());
        return ResponseEntity.ok(response);
    }

    @GetMapping("myLoans")
    public ResponseEntity<LoanListResponse> getMyLoans() {
        LoanListResponse response = new LoanListResponse();
        response.set(this.loanRepository.findAll());
        return ResponseEntity.ok(response);
    }

    @PostMapping("create")
    public ResponseEntity<Response<?>> create(@RequestBody Loan loan) {
        LoanResponse response = new LoanResponse();

        // Get logged in user
        User user = getLoggedInUser();
        if (user == null) {
            ErrorResponse error = new ErrorResponse();
            error.set("User not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        try {
            // Update loan object with logged in user, save to databse
            loan.setUser(user);
            response.set(this.loanRepository.save(loan));
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse();
            error.set("Bad request");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
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
