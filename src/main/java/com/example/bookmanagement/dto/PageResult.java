package com.example.bookmanagement.dto;

import java.util.List;

public record PageResult<T>(List<T> content, int page, int size, long total, int totalPages) {
}
