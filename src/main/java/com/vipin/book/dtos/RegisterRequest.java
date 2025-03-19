package com.vipin.book.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotEmpty(message = "firstname should not be null")
    @NotBlank(message = "firstname should not be null")
    private String firstname;

    @NotEmpty(message = "lastname should not be null")
    @NotBlank(message = "lastname should not be null")
    private String lastname;

    @Email(message = "email is not formatted")
    @NotEmpty(message = "email should not be null")
    @NotBlank(message = "email should not be null")
    private String email;

    @Size(min = 8, max = 16, message = "password should be in between 8 to 16 characters")
    @NotEmpty(message = "password should not be null")
    @NotBlank(message = "password should not be null")
    private String password;
}
