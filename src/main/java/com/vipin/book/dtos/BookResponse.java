package com.vipin.book.dtos;

import com.vipin.book.model.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {
    private Integer id;

    private String title;

    private String authorname;

    private String isbn;

    private String synopsis;

    private User owner;

    private byte[] cover;

    private Double rate;

    private Boolean archived;

    private Boolean shareable;

}
