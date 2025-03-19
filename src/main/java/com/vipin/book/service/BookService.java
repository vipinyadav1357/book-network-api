package com.vipin.book.service;

import com.vipin.book.book.Book;
import com.vipin.book.book.BookSpecification;
import com.vipin.book.dtos.BookRequest;
import com.vipin.book.dtos.BookResponse;
import com.vipin.book.dtos.BorrowedBookResponse;
import com.vipin.book.dtos.PageResponse;
import com.vipin.book.exceptionhandler.OperationNotPermittedException;
import com.vipin.book.history.BookTransactionHistory;
import com.vipin.book.mapper.BookMapper;
import com.vipin.book.model.User;
import com.vipin.book.repo.BookRepo;
import com.vipin.book.repo.BookTransactionRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookMapper bookMapper;
    private final BookRepo bookRepo;
    private final BookTransactionRepo bookTransactionRepo;
    private final FileStorageService fileStorageService;

    public Integer save(BookRequest request, @NotNull Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Book book = bookMapper.toBook(request);
        book.setOwner(user);
        return bookRepo.save(book).getId();
    }

    public BookResponse findById(Integer bookId) {
        return bookRepo.findById(bookId).map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("No Book available of this ID :: " + bookId));
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, @NotNull Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepo.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> bookResponses = books.stream().map(bookMapper::toBookResponse
        ).collect(Collectors.toList());
        return new PageResponse<BookResponse>(bookResponses, books.getNumber(), books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(), books.isFirst(), books.isLast());
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, @NotNull Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepo.findAll(BookSpecification.withOwnerId(user.getId()), pageable);
        List<BookResponse> bookResponses = books.stream().map(bookMapper::toBookResponse).collect(Collectors.toList());
        return new PageResponse<BookResponse>(bookResponses, books.getNumber(), books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(), books.isFirst(), books.isLast());
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, @NotNull Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = bookTransactionRepo.findAllBorrowedBooks(pageable,
                user.getId());
        List<BorrowedBookResponse> bookResponses = allBorrowedBooks.stream().map(bookMapper::toBoworrowedBooksResponse)
                .collect(Collectors.toList());
        return new PageResponse<BorrowedBookResponse>(bookResponses, allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(), allBorrowedBooks.isFirst(), allBorrowedBooks.isLast());
    }

    public PageResponse<BorrowedBookResponse> findAllReturenedBooks(int page, int size, @NotNull Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = bookTransactionRepo.findAllReturenedBooks(pageable,
                user.getId());
        List<BorrowedBookResponse> bookResponses = allBorrowedBooks.stream().map(bookMapper::toBoworrowedBooksResponse)
                .collect(Collectors.toList());
        return new PageResponse<BorrowedBookResponse>(bookResponses, allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(), allBorrowedBooks.isFirst(), allBorrowedBooks.isLast());
    }

    public Integer updateShareableStatus(Integer bookId, @NotNull Authentication connectedUser) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("no books is related to id: " + bookId));
        User user = (User) connectedUser.getPrincipal();
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("you can't update book shareable status");
        }
        book.setArchived(book.isShareable());
        System.out.println("archived" + book.isArchived());
        book.setShareable(!book.isShareable());
        System.out.println("shareable" + book.isShareable());
        bookRepo.save(book);
        return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, @NotNull Authentication connectedUser) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("no books is related to id: " + bookId));
        User user = (User) connectedUser.getPrincipal();
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("you can't update book archived status");
        }
        book.setShareable(book.isArchived());
        book.setArchived(!book.isArchived());
        bookRepo.save(book);
        return bookId;
    }

    public Integer borrowBook(Integer bookId, @NotNull Authentication connectedUser) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("no books is related to id: " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException(
                    "the requested book can't be borrowed because either it is archived or not shareable");
        }
        User user = (User) connectedUser.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("you can't borrow own book");
        }
        final boolean isAlreadyBorrowed = bookTransactionRepo.isAlreadyBorrowedByUser(bookId, user.getId());
        if (isAlreadyBorrowed) {
            throw new OperationNotPermittedException("The requested book is already borrowed");
        }
        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder().user(user).book(book)
                .returened(false).returenApproved(false).build();
        return bookTransactionRepo.save(bookTransactionHistory).getId();
    }

    public Integer returnBorrowedBook(Integer bookId, @NotNull Authentication connectedUser) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("no books is related to id: " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException(
                    "the requested book can't be returned because either it is archived or not shareable");
        }
        User user = (User) connectedUser.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("you can't return own book");
        }
        BookTransactionHistory bookTransactionHistory = bookTransactionRepo.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(
                        () -> new OperationNotPermittedException("no books is borrowed related to id: " + bookId));
        bookTransactionHistory.setReturened(true);
        return bookTransactionRepo.save(bookTransactionHistory).getId();
    }

    public Integer approveReturnBorrowedBook(Integer bookId, @NotNull Authentication connectedUser) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("no books is related to id: " + bookId));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException(
                    "the requested book can't be return approve because either it is archived or not shareable");
        }
        User user = (User) connectedUser.getPrincipal();
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("you can't approve return book of the different user");
        }
        BookTransactionHistory bookTransactionHistory = bookTransactionRepo.findByBookIdAndOwnerId(bookId, user.getId())
                .orElseThrow(
                        () -> new OperationNotPermittedException("the books is not return related to id: " + bookId));
        bookTransactionHistory.setReturenApproved(true);
        return bookTransactionRepo.save(bookTransactionHistory).getId();
    }

    public void uploadBookCoverPiture(MultipartFile file, @NotNull Authentication connectedUser, Integer bookId) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("no books is related to id: " + bookId));
        User user = (User) connectedUser.getPrincipal();
        var bookCover = fileStorageService.saveFile(file, user.getId());
        book.setBookCover(bookCover);
        bookRepo.save(book);
    }
}