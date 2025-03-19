package com.vipin.book.exceptionhandler;

import jakarta.mail.MessagingException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NotNull LockedException lockedException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder().bussinessErrorCode(BussinessErrorCodes.ACCOUNT_LOCKED.getCode())
                        .bussinessErrorDescription(BussinessErrorCodes.ACCOUNT_LOCKED.getDescription())
                        .error(lockedException.getMessage()).build());
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NotNull DisabledException disabledException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder().bussinessErrorCode(BussinessErrorCodes.ACCOUNT_DISABLED.getCode())
                        .bussinessErrorDescription(BussinessErrorCodes.ACCOUNT_DISABLED.getDescription())
                        .error(disabledException.getMessage()).build());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NotNull BadCredentialsException badCredentialsException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder().bussinessErrorCode(BussinessErrorCodes.BAD_CREDENTIALS.getCode())
                        .bussinessErrorDescription(BussinessErrorCodes.BAD_CREDENTIALS.getDescription())
                        .error(badCredentialsException.getMessage()).build());
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NotNull MessagingException messagingException) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.builder().error(messagingException.getMessage()).build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NotNull
                                                             MethodArgumentNotValidException methodArgumentNotValidException) {
        Set<String> errors = new HashSet<>();
        methodArgumentNotValidException.getBindingResult().getAllErrors().forEach(error -> {
            var errorMsg = error.getDefaultMessage();
            errors.add(errorMsg);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder().validationErrors(errors).build());
    }

    @ExceptionHandler(OperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NotNull
                                                             OperationNotPermittedException operationNotPermittedException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder().error(operationNotPermittedException.getMessage()).build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(@NotNull Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ExceptionResponse.builder()
                .bussinessErrorDescription("Internal error, please contact the admin ").error(exception.getMessage())
                .build());
    }

}
