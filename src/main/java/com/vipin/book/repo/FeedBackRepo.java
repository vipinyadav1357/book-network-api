package com.vipin.book.repo;

import com.vipin.book.feedback.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedBackRepo extends JpaRepository<Feedback, Integer> {

    @Query("""
            SELECT feedback
            FROM Feedback feedback
            WHERE feedback.book.id=:bookId
            """)
    Page<Feedback> findAllByBookId(int bookId, Pageable pageable);

}
