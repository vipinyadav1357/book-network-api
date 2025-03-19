package com.vipin.book.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String toekn;

    private LocalDateTime createdat;
    private LocalDateTime expiredat;
    private LocalDateTime validatedat;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;
}
