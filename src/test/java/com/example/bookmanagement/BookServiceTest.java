package com.example.bookmanagement;

import com.example.bookmanagement.dto.PageResult;
import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.mapper.BookMapper;
import com.example.bookmanagement.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    @Test
    void findBooks_shouldReturnPagedResult() {
        Book book = new Book();
        book.setId(1L);
        book.setName("Test Book");

        when(bookMapper.findByKeyword(anyString(), anyInt(), anyInt())).thenReturn(List.of(book));
        when(bookMapper.countByKeyword(anyString())).thenReturn(25L);

        PageResult<Book> result = bookService.findBooks("test", 2, 10);

        assertEquals(1, result.content().size());
        assertEquals(2, result.page());
        assertEquals(10, result.size());
        assertEquals(25L, result.total());
        assertEquals(3, result.totalPages());
    }
}
