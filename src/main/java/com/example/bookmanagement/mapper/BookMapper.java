package com.example.bookmanagement.mapper;

import com.example.bookmanagement.entity.Book;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface BookMapper {

    @Select("SELECT * FROM books ORDER BY create_time DESC")
    List<Book> findAll();

    @Select("SELECT * FROM books WHERE id = #{id}")
    Book findById(@Param("id") Long id);

    @Insert("INSERT INTO books(name, category, author, publisher, version) VALUES(#{name}, #{category}, #{author}, #{publisher}, #{version})")
    int insert(Book book);

    @Update("UPDATE books SET name = #{name}, category = #{category}, author = #{author}, publisher = #{publisher}, version = #{version} WHERE id = #{id}")
    int update(Book book);

    @Select("""
            <script>
            SELECT * FROM books
            <where>
                <if test='keyword != null and keyword != ""'>
                    name LIKE CONCAT('%', #{keyword}, '%') OR author LIKE CONCAT('%', #{keyword}, '%')
                </if>
            </where>
            ORDER BY create_time DESC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<Book> findByKeyword(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);

    @Select("""
            <script>
            SELECT COUNT(*) FROM books
            <where>
                <if test='keyword != null and keyword != ""'>
                    name LIKE CONCAT('%', #{keyword}, '%') OR author LIKE CONCAT('%', #{keyword}, '%')
                </if>
            </where>
            </script>
            """)
    long countByKeyword(@Param("keyword") String keyword);

    @Delete("DELETE FROM books WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
