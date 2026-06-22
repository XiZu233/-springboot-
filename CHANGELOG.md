# 更新日志

## 版本 1.1.0 — 2026-06-22

### 概述

本次更新在原有登录注册和图书增删基础上，补齐了教学演示系统常见的核心能力：角色权限控制、完整 CRUD、服务端校验、全局异常处理、搜索分页以及单元测试。所有改动均围绕现有 Spring Boot + Spring Security + Thymeleaf + MyBatis 技术栈展开，没有引入前后端分离、AOP、文件上传等超出课程设计范围的复杂度。

---

## 一、角色权限控制（RBAC）

### 1.1 实现目标

让 `users` 表中的 `role` 字段真正生效：`ADMIN` 可以新增、编辑、删除图书；`USER` 只能查看列表和详情。

### 1.2 实现方式

1. **开启方法级安全**

   在 [SecurityConfig.java](src/main/java/com/example/bookmanagement/config/SecurityConfig.java) 上添加：

   ```java
   @EnableMethodSecurity(prePostEnabled = true)
   ```

   这样 `@PreAuthorize` 注解才能在 Controller 方法上生效。

2. **保护管理端点**

   在 [BookController.java](src/main/java/com/example/bookmanagement/controller/BookController.java) 中，对新增、编辑、删除端点加注解：

   ```java
   @PreAuthorize("hasRole('ADMIN')")
   ```

   涉及的方法包括：

   - `GET /books/add`
   - `POST /books/add`
   - `POST /books/delete/{id}`
   - `GET /books/{id}/edit`
   - `POST /books/{id}/edit`

3. **前端按钮按角色显示**

   在 [books/list.html](src/main/resources/templates/books/list.html)、[books/add.html](src/main/resources/templates/books/add.html)、[books/detail.html](src/main/resources/templates/books/detail.html)、[books/edit.html](src/main/resources/templates/books/edit.html) 中，使用 Thymeleaf Spring Security 扩展：

   ```html
   <div sec:authorize="hasRole('ADMIN')">...添加/编辑/删除按钮...</div>
   ```

   这样即使用户直接输入 URL，后端也会拒绝；前端也不会显示无权限的入口。

### 1.3 涉及文件

- `src/main/java/com/example/bookmanagement/config/SecurityConfig.java`
- `src/main/java/com/example/bookmanagement/controller/BookController.java`
- `src/main/resources/templates/books/list.html`
- `src/main/resources/templates/books/add.html`
- `src/main/resources/templates/books/detail.html`
- `src/main/resources/templates/books/edit.html`

---

## 二、完整图书 CRUD

### 2.1 实现目标

原系统只有新增、列表、删除，缺少查看详情和编辑修改。本次补齐。

### 2.2 实现方式

1. **Mapper 层**

   在 [BookMapper.java](src/main/java/com/example/bookmanagement/mapper/BookMapper.java) 新增 `update` 方法：

   ```java
   @Update("UPDATE books SET name = #{name}, category = #{category}, author = #{author}, publisher = #{publisher}, version = #{version} WHERE id = #{id}")
   int update(Book book);
   ```

2. **Service 层**

   在 [BookService.java](src/main/java/com/example/bookmanagement/service/BookService.java) 新增：

   ```java
   public void updateBook(Book book) {
       bookMapper.update(book);
   }
   ```

3. **Controller 层**

   在 [BookController.java](src/main/java/com/example/bookmanagement/controller/BookController.java) 新增三个端点：

   - `GET /books/{id}`：详情页
   - `GET /books/{id}/edit`：编辑表单页
   - `POST /books/{id}/edit`：保存编辑

   其中编辑和删除仍受 `@PreAuthorize("hasRole('ADMIN')")` 保护。

4. **模板层**

   - 新增 [books/detail.html](src/main/resources/templates/books/detail.html)：卡片式展示图书全部字段。
   - 新增 [books/edit.html](src/main/resources/templates/books/edit.html)：预填充数据的编辑表单。
   - 修改 [books/list.html](src/main/resources/templates/books/list.html)：每行增加“详情”“编辑”按钮。

### 2.3 涉及文件

- `src/main/java/com/example/bookmanagement/controller/BookController.java`
- `src/main/java/com/example/bookmanagement/service/BookService.java`
- `src/main/java/com/example/bookmanagement/mapper/BookMapper.java`
- `src/main/resources/templates/books/detail.html`
- `src/main/resources/templates/books/edit.html`
- `src/main/resources/templates/books/list.html`

---

## 三、服务端数据校验

### 3.1 实现目标

原系统仅依赖前端 `required`，后端无校验。本次新增 Jakarta Bean Validation，确保非法数据无法入库，并在表单页回显错误。

### 3.2 实现方式

1. **引入依赖**

   在 [pom.xml](pom.xml) 中添加：

   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-validation</artifactId>
   </dependency>
   ```

2. **实体加注解**

   在 [Book.java](src/main/java/com/example/bookmanagement/entity/Book.java) 中：

   ```java
   @NotBlank(message = "图书名称不能为空")
   @Size(max = 100, message = "图书名称不能超过 100 个字符")
   private String name;
   ```

   在 [RegisterRequest.java](src/main/java/com/example/bookmanagement/dto/RegisterRequest.java) 中：

   ```java
   @NotBlank(message = "用户名不能为空")
   @Size(min = 3, max = 20, message = "用户名长度必须在 3-20 个字符之间")
   private String username;
   ```

3. **Controller 使用 BindingResult**

   在 [BookController.java](src/main/java/com/example/bookmanagement/controller/BookController.java) 和 [AuthController.java](src/main/java/com/example/bookmanagement/controller/AuthController.java) 中：

   ```java
   public String add(@Valid @ModelAttribute("book") Book book, BindingResult bindingResult, Model model) {
       if (bindingResult.hasErrors()) {
           return "books/add";
       }
       ...
   }
   ```

4. **模板回显错误**

   在 [books/add.html](src/main/resources/templates/books/add.html)、[books/edit.html](src/main/resources/templates/books/edit.html)、[register.html](src/main/resources/templates/register.html) 中：

   ```html
   <div class="text-danger small mt-1" th:if="${#fields.hasErrors('name')}" th:errors="*{name}"></div>
   ```

### 3.3 涉及文件

- `pom.xml`
- `src/main/java/com/example/bookmanagement/entity/Book.java`
- `src/main/java/com/example/bookmanagement/dto/RegisterRequest.java`
- `src/main/java/com/example/bookmanagement/controller/AuthController.java`
- `src/main/java/com/example/bookmanagement/controller/BookController.java`
- `src/main/resources/templates/books/add.html`
- `src/main/resources/templates/books/edit.html`
- `src/main/resources/templates/register.html`

---

## 四、全局异常处理与友好错误页

### 4.1 实现目标

避免异常直接暴露堆栈给用户，并为权限不足、请求错误、系统错误提供统一提示页。

### 4.2 实现方式

1. **全局异常处理器**

   新增 [GlobalExceptionHandler.java](src/main/java/com/example/bookmanagement/exception/GlobalExceptionHandler.java)，使用 `@ControllerAdvice`：

   ```java
   @ExceptionHandler(AccessDeniedException.class)
   public String handleAccessDeniedException(AccessDeniedException ex, Model model) { ... }

   @ExceptionHandler(IllegalArgumentException.class)
   public String handleIllegalArgumentException(IllegalArgumentException ex, Model model) { ... }

   @ExceptionHandler(Exception.class)
   public String handleException(Exception ex, Model model) { ... }
   ```

   `IllegalArgumentException` 用于业务参数错误（如访问不存在的图书），`AccessDeniedException` 用于权限不足。

2. **错误页面模板**

   新增 [error.html](src/main/resources/templates/error.html)：

   - 接收 `errorTitle` 和 `errorMessage`；
   - 提供“返回首页”和“图书列表”链接。

### 4.3 涉及文件

- `src/main/java/com/example/bookmanagement/exception/GlobalExceptionHandler.java`
- `src/main/resources/templates/error.html`

---

## 五、图书搜索与分页

### 5.1 实现目标

图书列表支持按名称或作者关键字搜索，并支持分页显示，避免一次性加载全部数据。

### 5.2 实现方式

1. **分页结果对象**

   新增 [PageResult.java](src/main/java/com/example/bookmanagement/dto/PageResult.java)：

   ```java
   public record PageResult<T>(List<T> content, int page, int size, long total, int totalPages) { }
   ```

2. **动态 SQL**

   在 [BookMapper.java](src/main/java/com/example/bookmanagement/mapper/BookMapper.java) 使用 MyBatis `@Select` + `<script>` 实现动态条件：

   ```java
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
   ```

   同时新增 `countByKeyword` 统计总数。

3. **Service 层分页计算**

   在 [BookService.java](src/main/java/com/example/bookmanagement/service/BookService.java) 中：

   ```java
   public PageResult<Book> findBooks(String keyword, int page, int size) {
       int offset = (page - 1) * size;
       List<Book> content = bookMapper.findByKeyword(keyword, offset, size);
       long total = bookMapper.countByKeyword(keyword);
       int totalPages = (int) Math.ceil((double) total / size);
       return new PageResult<>(content, page, size, total, totalPages);
   }
   ```

4. **Controller 接收参数**

   在 [BookController.java](src/main/java/com/example/bookmanagement/controller/BookController.java) 中：

   ```java
   public String list(@RequestParam(defaultValue = "") String keyword,
                      @RequestParam(defaultValue = "1") int page,
                      @RequestParam(defaultValue = "10") int size,
                      Model model) { ... }
   ```

   对 `page` 和 `size` 做了边界校验。

5. **前端搜索框与分页导航**

   在 [books/list.html](src/main/resources/templates/books/list.html) 中新增搜索表单和 Bootstrap 分页组件，分页链接保留当前 `keyword`。

### 5.3 涉及文件

- `src/main/java/com/example/bookmanagement/dto/PageResult.java`
- `src/main/java/com/example/bookmanagement/mapper/BookMapper.java`
- `src/main/java/com/example/bookmanagement/service/BookService.java`
- `src/main/java/com/example/bookmanagement/controller/BookController.java`
- `src/main/resources/templates/books/list.html`

---

## 六、单元测试

### 6.1 实现目标

为新增的校验规则和分页逻辑补充测试。

### 6.2 实现方式

1. **校验测试**

   新增 [BookValidationTest.java](src/test/java/com/example/bookmanagement/BookValidationTest.java)，直接使用 `Validator` 工厂：

   ```java
   Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
   ```

   覆盖：空白书名、书名过长、合法图书、非法注册请求。

2. **Service 测试**

   新增 [BookServiceTest.java](src/test/java/com/example/bookmanagement/BookServiceTest.java)，使用 Mockito：

   ```java
   @ExtendWith(MockitoExtension.class)
   public class BookServiceTest { ... }
   ```

   Mock `BookMapper`，验证 `findBooks` 返回的 `PageResult` 字段正确。

### 6.3 涉及文件

- `src/test/java/com/example/bookmanagement/BookValidationTest.java`
- `src/test/java/com/example/bookmanagement/BookServiceTest.java`

---

## 七、依赖变更

| 依赖 | 变更 | 用途 |
|------|------|------|
| `spring-boot-starter-validation` | 新增 | Jakarta Bean Validation |

其余依赖（Spring Boot、Spring Security、Thymeleaf、MyBatis、MySQL 驱动）无变更。

---

## 八、数据库变更

本次无表结构变更，仍使用原 `users` 表和 `books` 表：

- `users` 表的 `role` 字段已被用于区分 `ADMIN` / `USER`。
- `books` 表字段不变，新增 `UPDATE` 操作只修改已有列。

如需初始化数据库，仍执行：

```bash
mysql -uroot -p123456 < book-management.sql
```

---

## 九、配置文件说明

[application.yml](src/main/resources/application.yml) 中数据库密码保持教学默认 `123456`，与 README、部署文档一致。本地开发若使用其他密码，请修改后运行，但请勿将个人密码提交到仓库。

---

## 十、API 端点与权限矩阵

| 端点 | 方法 | 权限 | 说明 |
|------|------|------|------|
| `/` | GET | 公开 | 首页 |
| `/login` | GET/POST | 公开 | 登录 |
| `/register` | GET/POST | 公开 | 注册 |
| `/books` | GET | 已登录 | 图书列表（支持 `keyword`、`page`、`size`） |
| `/books/{id}` | GET | 已登录 | 图书详情 |
| `/books/add` | GET | ADMIN | 新增图书表单 |
| `/books/add` | POST | ADMIN | 保存新增图书 |
| `/books/{id}/edit` | GET | ADMIN | 编辑图书表单 |
| `/books/{id}/edit` | POST | ADMIN | 保存编辑 |
| `/books/delete/{id}` | POST | ADMIN | 删除图书 |
| `/logout` | POST | 已登录 | 退出登录 |

---

## 十一、验证方式

1. 编译测试：

   ```bash
   mvn clean test
   ```

2. 打包：

   ```bash
   mvn clean package -DskipTests
   ```

3. 运行并访问：

   ```bash
   java -jar target/book-management-0.0.1-SNAPSHOT.jar
   ```

   浏览器打开 `http://localhost:8080`。

4. 手工验证 checklist：

   - admin/123456 登录后能看到“添加图书”“编辑”“删除”按钮；
   - 普通用户注册登录后只能看到“详情”按钮；
   - 普通用户直接访问 `/books/add` 显示“权限不足”；
   - 新增、编辑、删除图书正常；
   - 搜索框可按书名或作者搜索；
   - 分页导航正常；
   - 表单提交非法数据时显示校验错误。

---

## 十二、后续可扩展方向

- 借阅/归还流程：新增 `borrow_records` 表和对应 CRUD；
- 图书分类字典表：将 `category` 从字符串改为外键关联；
- 操作日志：使用 AOP 记录管理员的关键操作；
- 密码找回/邮箱验证：引入邮件服务。

以上扩展均超出本次课程设计范围，可按教学需要逐步添加。
