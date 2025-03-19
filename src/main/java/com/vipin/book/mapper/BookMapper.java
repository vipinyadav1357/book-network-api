package com.vipin.book.mapper;

import com.vipin.book.book.Book;
import com.vipin.book.dtos.BookRequest;
import com.vipin.book.dtos.BookResponse;
import com.vipin.book.dtos.BorrowedBookResponse;
import com.vipin.book.file.FileUtils;
import com.vipin.book.history.BookTransactionHistory;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {
    public Book toBook(BookRequest request) {

        return Book.builder().id(request.id()).title(request.title()).authorName(request.authorName())
                .isbn(request.isbn()).synopsis(request.synopsis()).shareable(request.shareable())
                .archived(!request.shareable()).build();
    }

    public BookResponse toBookResponse(Book book) {
        return BookResponse.builder().id(book.getId()).title(book.getTitle()).authorname(book.getAuthorName())
                .isbn(book.getIsbn()).owner(book.getOwner()).rate(book.getRate())
                .shareable(book.isShareable()).archived(book.isArchived()).synopsis(book.getSynopsis())
                .cover(FileUtils.readFileFromLocation(book.getBookCover())).build();
    }

    public BorrowedBookResponse toBoworrowedBooksResponse(BookTransactionHistory history) {
        return BorrowedBookResponse.builder().id(history.getBook().getId()).title(history.getBook().getTitle())
                .authorname(history.getBook().getAuthorName())
                .isbn(history.getBook().getIsbn()).rate(history.getBook().getRate())
                .returened(history.isReturened()).returenedApproved(history.isReturenApproved()).build();
    }
}
