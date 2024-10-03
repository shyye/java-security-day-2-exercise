package com.booleanuk.api.controllers;

import com.booleanuk.api.payload.responses.LoanListResponse;
import com.booleanuk.api.repositories.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("loans")
public class LoanController {
    @Autowired
    private LoanRepository loanRepository;

    @GetMapping
    public ResponseEntity<LoanListResponse> getAll() {
        LoanListResponse response = new LoanListResponse();
        response.set(this.loanRepository.findAll());
        return ResponseEntity.ok(response);
    }
}
