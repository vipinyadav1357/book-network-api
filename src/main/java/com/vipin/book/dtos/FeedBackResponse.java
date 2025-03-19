package com.vipin.book.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedBackResponse {
    private double note;
    private String comment;
    private boolean ownFeedback;
}
