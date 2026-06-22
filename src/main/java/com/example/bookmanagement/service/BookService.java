package com.example.bookmanagement.service;

import com.example.bookmanagement.dto.PageResult;
import com.example.bookmanagement.entity.Book;
import com.example.bookmanagement.mapper.BookMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookMapper bookMapper;

    public BookService(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    public List<Book> findAll() {
        return bookMapper.findAll();
    }

    public Book findById(Long id) {
        return bookMapper.findById(id);
    }

    public void addBook(Book book) {
        bookMapper.insert(book);
    }

    public void updateBook(Book book) {
        bookMapper.update(book);
    }

    public void deleteBook(Long id) {
        bookMapper.deleteById(id);
    }

    public PageResult<Book> findBooks(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        List<Book> content = bookMapper.findByKeyword(keyword, offset, size);
        long total = bookMapper.countByKeyword(keyword);
        int totalPages = (int) Math.ceil((double) total / size);
        return new PageResult<>(content, page, size, total, totalPages);
    }
}
