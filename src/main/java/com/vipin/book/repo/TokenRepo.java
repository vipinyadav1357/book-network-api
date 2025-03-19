package com.vipin.book.repo;

import com.vipin.book.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepo extends JpaRepository<Token, Integer> {
    Optional<Token> findByToekn(String toeknno);

}
