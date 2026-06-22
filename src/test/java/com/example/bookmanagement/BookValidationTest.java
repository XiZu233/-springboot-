package com.example.bookmanagement;

import com.example.bookmanagement.dto.RegisterRequest;
import com.example.bookmanagement.entity.Book;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void bookNameBlank_shouldFail() {
        Book book = new Book();
        book.setName("");
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertFalse(violations.isEmpty());
    }

    @Test
    void bookNameTooLong_shouldFail() {
        Book book = new Book();
        book.setName("a".repeat(101));
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validBook_shouldPass() {
        Book book = new Book();
        book.setName("Effective Java");
        book.setAuthor("Joshua Bloch");
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertTrue(violations.isEmpty());
    }

    @Test
    void registerPasswordMismatch_shouldStillValidateFields() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("ab");
        request.setPassword("123");
        request.setConfirmPassword("456");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }
}
