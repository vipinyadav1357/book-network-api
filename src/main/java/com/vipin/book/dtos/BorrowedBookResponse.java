package com.vipin.book.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BorrowedBookResponse {
    private Integer id;

    private String title;

    private String authorname;

    private String isbn;

    private Double rate;

    private Boolean returened;

    private Boolean returenedApproved;

}
