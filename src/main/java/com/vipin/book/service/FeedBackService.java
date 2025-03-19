package com.vipin.book.service;

import com.vipin.book.book.Book;
import com.vipin.book.dtos.FeedBackRequest;
import com.vipin.book.dtos.FeedBackResponse;
import com.vipin.book.dtos.PageResponse;
import com.vipin.book.exceptionhandler.OperationNotPermittedException;
import com.vipin.book.feedback.Feedback;
import com.vipin.book.mapper.FeedbackMapper;
import com.vipin.book.model.User;
import com.vipin.book.repo.BookRepo;
import com.vipin.book.repo.FeedBackRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedBackService {

    private final BookRepo bookRepo;
    private final FeedbackMapper feedbackMapper;
    private final FeedBackRepo feedBackRepo;

    public Integer saveFeedBack(@Valid @NotNull FeedBackRequest request, Authentication connectedUser) {
        Book book = bookRepo.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("no books is related to id: " + request.bookId()));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException(
                    "the requested book can't be give feedback because either it is archived or not shareable");
        }
        User user = (User) connectedUser.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("you can't give feedback to own book");
        }
        Feedback feedback = feedbackMapper.toFeedback(request, book);
        return feedBackRepo.save(feedback).getId();
    }

    public PageResponse<FeedBackResponse> findAllFeedBackByBook(int bookId, int page, int size,
                                                                @NotNull Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        Page<Feedback> feedbacks = feedBackRepo.findAllByBookId(bookId, pageable);
        List<FeedBackResponse> feedBackResponses = feedbacks.stream()
                .map(f -> feedbackMapper.toFeedBackResponse(f, user.getId()))
                .collect(Collectors.toList());
        return new PageResponse<FeedBackResponse>(feedBackResponses, feedbacks.getNumber(), feedbacks.getSize(),
                feedbacks.getTotalElements(), feedbacks.getTotalPages(), feedbacks.isFirst(), feedbacks.isLast());
    }

}
