package com.booleanuk.api.repositories;

import com.booleanuk.api.models.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Integer> {
    List<Loan> findAllByStatus(String status);
}
