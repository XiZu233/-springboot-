package com.example.bookmanagement.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class Book {

    private Long id;

    @NotBlank(message = "图书名称不能为空")
    @Size(max = 100, message = "图书名称不能超过 100 个字符")
    private String name;

    @Size(max = 50, message = "类别不能超过 50 个字符")
    private String category;

    @Size(max = 50, message = "作者不能超过 50 个字符")
    private String author;

    @Size(max = 100, message = "出版社不能超过 100 个字符")
    private String publisher;

    @Size(max = 20, message = "版本不能超过 20 个字符")
    private String version;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
