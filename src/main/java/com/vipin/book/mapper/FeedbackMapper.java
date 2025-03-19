package com.vipin.book.mapper;

import com.vipin.book.book.Book;
import com.vipin.book.dtos.FeedBackRequest;
import com.vipin.book.dtos.FeedBackResponse;
import com.vipin.book.feedback.Feedback;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {

    public Feedback toFeedback(@Valid FeedBackRequest request, Book book) {

        return Feedback.builder().note(request.note()).comment(request.comment()).book(book).build();
    }

    public FeedBackResponse toFeedBackResponse(Feedback feedback, Integer userId) {
        return FeedBackResponse.builder().note(feedback.getNote()).comment(feedback.getComment())
                .ownFeedback(Objects.equals(feedback.getCreatedBy(), userId)).build();
    }
}
