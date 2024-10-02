package com.booleanuk.api.repositories;

import com.booleanuk.api.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Integer> {
}
