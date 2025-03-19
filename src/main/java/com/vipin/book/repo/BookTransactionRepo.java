package com.vipin.book.repo;

import com.vipin.book.history.BookTransactionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookTransactionRepo extends JpaRepository<BookTransactionHistory, Integer> {
    @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.user.id=:userId
            """)
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Integer userId);

    @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.book.owner.id=:userId
            """)
    Page<BookTransactionHistory> findAllReturenedBooks(Pageable pageable, Integer userId);

    @Query("""
            SELECT
            (COUNT(*)>0) As isBorrowed
            FROM BookTransactionHistory bookTransactionHistory
            WHERE bookTransactionHistory.user.id=:userId
            AND bookTransactionHistory.book.id=:bookId
            AND bookTransactionHistory.returenApproved=false
            """)
    boolean isAlreadyBorrowedByUser(Integer bookId, Integer userId);

    @Query("""
            SELECT transaction
            FROM BookTransactionHistory transaction
            WHERE transaction.book.id=:bookId
            AND transaction.user.id=:userId
            AND transaction.returened=false
            AND transaction.returenApproved=false
            """)
    Optional<BookTransactionHistory> findByBookIdAndUserId(Integer bookId, Integer userId);

    @Query("""
            SELECT transaction
            FROM BookTransactionHistory transaction
            WHERE transaction.book.id=:bookId
            AND transaction.book.owner.id=:userId
            AND transaction.returened=true
            AND transaction.returenApproved=false
            """)
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(Integer bookId, Integer userId);

}
