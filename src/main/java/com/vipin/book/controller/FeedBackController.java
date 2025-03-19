package com.vipin.book.controller;

import com.vipin.book.dtos.FeedBackRequest;
import com.vipin.book.dtos.FeedBackResponse;
import com.vipin.book.dtos.PageResponse;
import com.vipin.book.service.FeedBackService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feedbacks")
@RequiredArgsConstructor
@Tag(name = "feedbacks")
public class FeedBackController {
    @Autowired
    private final FeedBackService feedBackService;

    @PostMapping("/savefeedback")
    public ResponseEntity<Integer> saveFeedBack(@Valid @RequestBody FeedBackRequest feedBackRequest,
                                                Authentication connectedUser) {
        return ResponseEntity.ok(feedBackService.saveFeedBack(feedBackRequest, connectedUser));
    }

    @PostMapping("/book/{book-id}")
    public ResponseEntity<PageResponse<FeedBackResponse>> findAllFeedBackByBook(@PathVariable("book-id") int bookId,
                                                                                @RequestParam(defaultValue = "0", required = false) int page,
                                                                                @RequestParam(defaultValue = "10", required = false) int size,
                                                                                Authentication connectedUser) {
        return ResponseEntity.ok(feedBackService.findAllFeedBackByBook(bookId, page, size, connectedUser));
    }
}
