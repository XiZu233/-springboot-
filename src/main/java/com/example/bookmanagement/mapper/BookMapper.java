package com.example.bookmanagement.mapper;

import com.example.bookmanagement.entity.Book;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BookMapper {

    @Select("SELECT * FROM books ORDER BY create_time DESC")
    List<Book> findAll();

    @Select("SELECT * FROM books WHERE id = #{id}")
    Book findById(@Param("id") Long id);

    @Insert("INSERT INTO books(name, category, author, publisher, version) VALUES(#{name}, #{category}, #{author}, #{publisher}, #{version})")
    int insert(Book book);

    @Delete("DELETE FROM books WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
