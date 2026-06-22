# JavaWeb 实训任务书 —— 图书管理系统改造篇

## 实训说明

本任务书基于 [JavaWeb实训指导书.md](JavaWeb实训指导书.md) 中的原始项目，带领学习者从零开始逐步完善系统。每个任务都包含：

- **任务目标**：要达成什么效果。
- **前置状态**：开始任务前代码应该是什么样子。
- **关键知识点**：需要理解的概念。
- **修改步骤**：具体要改哪些文件、怎么改。
- **关键代码**：可直接对照的代码片段。
- **验证方法**：改完后如何确认成功。

## 环境准备

1. 克隆或导入项目到 IntelliJ IDEA。
2. 安装 JDK 17+ 和 MySQL 8.0。
3. 执行 [book-management.sql](book-management.sql) 初始化数据库。
4. 修改 [application.yml](src/main/resources/application.yml) 中的数据库密码为你本地 MySQL 的 root 密码。
5. 运行 `mvn clean package -DskipTests` 打包。
6. 运行 `java -jar target/book-management-0.0.1-SNAPSHOT.jar` 启动。
7. 浏览器访问 `http://localhost:8080`，使用 `admin/123456` 登录，确认原始功能正常。

---

## 任务 1：角色权限控制（RBAC）

### 任务目标

让系统中的 `ADMIN` 和 `USER` 角色真正生效：

- `ADMIN`：可以新增、编辑、删除图书。
- `USER`：只能查看图书列表和详情。

### 前置状态

原始项目的 `users` 表已有 `role` 字段，`UserDetailsServiceImpl` 也会把角色写入 Spring Security 上下文，但 `SecurityConfig` 没有开启方法级权限控制，所有登录用户都能执行任何操作。

### 关键知识点

- `@EnableMethodSecurity(prePostEnabled = true)`：开启方法级安全注解。
- `@PreAuthorize("hasRole('ADMIN')")`：限制只有 ADMIN 能调用该方法。
- `sec:authorize="hasRole('ADMIN')"`：Thymeleaf 中按角色控制页面元素显示。

### 修改步骤

#### 步骤 1.1：开启方法级安全

打开 [SecurityConfig.java](src/main/java/com/example/bookmanagement/config/SecurityConfig.java)，在类上添加注解：

```java
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig { ... }
```

#### 步骤 1.2：保护管理端点

打开 [BookController.java](src/main/java/com/example/bookmanagement/controller/BookController.java)，在新增、编辑、删除方法上加：

```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/add")
public String addForm(Model model) { ... }

@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/add")
public String add(...) { ... }

@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/delete/{id}")
public String delete(...) { ... }

@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/{id}/edit")
public String editForm(...) { ... }

@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/{id}/edit")
public String update(...) { ... }
```

#### 步骤 1.3：前端隐藏无权限按钮

打开 [books/list.html](src/main/resources/templates/books/list.html)：

```html
<div class="mb-3" sec:authorize="hasRole('ADMIN')">
    <a class="btn btn-primary" href="/books/add" role="button">+ 添加图书</a>
</div>

<td>
    <a th:href="@{/books/{id}(id=${book.id})}" class="btn btn-info btn-sm">详情</a>
    <a sec:authorize="hasRole('ADMIN')" th:href="@{/books/{id}/edit(id=${book.id})}" class="btn btn-warning btn-sm ms-1">编辑</a>
    <form sec:authorize="hasRole('ADMIN')" th:action="@{/books/delete/{id}(id=${book.id})}" method="post" style="display:inline;">
        <button type="submit" class="btn btn-danger btn-sm ms-1">删除</button>
    </form>
</td>
```

同时在 [books/add.html](src/main/resources/templates/books/add.html)、[books/detail.html](src/main/resources/templates/books/detail.html)、[books/edit.html](src/main/resources/templates/books/edit.html) 的导航栏中，对“添加图书”链接加 `sec:authorize="hasRole('ADMIN')"`。

### 验证方法

1. 使用 `admin/123456` 登录，确认能看到“添加图书”“编辑”“删除”按钮。
2. 注册一个普通用户并登录，确认只能看到“详情”按钮。
3. 普通用户直接访问 `http://localhost:8080/books/add`，应被阻止。

---

## 任务 2：图书详情与编辑功能

### 任务目标

补齐 CRUD 中的“查看详情”和“编辑修改”。

### 前置状态

系统只有新增、列表、删除，没有详情和编辑。

### 关键知识点

- `@GetMapping("/{id}")`：RESTful 风格的 URL 参数。
- `@PathVariable Long id`：从 URL 中提取 id。
- `bookService.findById(id)`：根据 id 查询单个对象。
- `bookMapper.update(book)`：更新数据库记录。

### 修改步骤

#### 步骤 2.1：Mapper 新增 update 方法

打开 [BookMapper.java](src/main/java/com/example/bookmanagement/mapper/BookMapper.java)：

```java
@Update("UPDATE books SET name = #{name}, category = #{category}, author = #{author}, publisher = #{publisher}, version = #{version} WHERE id = #{id}")
int update(Book book);
```

#### 步骤 2.2：Service 新增 updateBook 方法

打开 [BookService.java](src/main/java/com/example/bookmanagement/service/BookService.java)：

```java
public void updateBook(Book book) {
    bookMapper.update(book);
}
```

#### 步骤 2.3：Controller 新增端点

打开 [BookController.java](src/main/java/com/example/bookmanagement/controller/BookController.java)：

```java
@GetMapping("/{id}")
public String detail(@PathVariable Long id, Model model) {
    Book book = bookService.findById(id);
    if (book == null) {
        throw new IllegalArgumentException("图书不存在：" + id);
    }
    model.addAttribute("book", book);
    return "books/detail";
}

@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/{id}/edit")
public String editForm(@PathVariable Long id, Model model) {
    Book book = bookService.findById(id);
    if (book == null) {
        throw new IllegalArgumentException("图书不存在：" + id);
    }
    model.addAttribute("book", book);
    return "books/edit";
}

@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/{id}/edit")
public String update(@PathVariable Long id, @ModelAttribute Book book) {
    book.setId(id);
    bookService.updateBook(book);
    return "redirect:/books";
}
```

#### 步骤 2.4：新增详情页

创建 [books/detail.html](src/main/resources/templates/books/detail.html)：

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
<head>
    <meta charset="UTF-8">
    <title>图书详情</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<!-- 导航栏 -->
<div class="container mt-4">
    <h2>图书详情</h2>
    <div class="card">
        <div class="card-body">
            <h5 class="card-title" th:text="${book.name}"></h5>
            <p><strong>作者：</strong> <span th:text="${book.author} ?: '无'"></span></p>
            <p><strong>出版社：</strong> <span th:text="${book.publisher} ?: '无'"></span></p>
            <p><strong>版本：</strong> <span th:text="${book.version} ?: '无'"></span></p>
        </div>
    </div>
    <a sec:authorize="hasRole('ADMIN')" th:href="@{/books/{id}/edit(id=${book.id})}" class="btn btn-warning">编辑</a>
    <a href="/books" class="btn btn-secondary ms-2">返回列表</a>
</div>
</body>
</html>
```

#### 步骤 2.5：新增编辑页

创建 [books/edit.html](src/main/resources/templates/books/edit.html)，结构与 [books/add.html](src/main/resources/templates/books/add.html) 类似，但表单的 `th:action` 指向 `@{/books/{id}/edit(id=${book.id})}`，且输入框使用 `th:field="*{name}"` 预填充数据。

### 验证方法

1. 在图书列表点击“详情”，能正确显示图书信息。
2. 点击“编辑”，进入预填充的编辑表单。
3. 修改图书名称并保存，列表中对应记录更新。

---

## 任务 3：服务端数据校验

### 任务目标

防止非法数据进入数据库，并在表单页显示具体错误信息。

### 前置状态

系统只依赖前端 `required` 属性，后端无校验。

### 关键知识点

- `spring-boot-starter-validation`：Jakarta Bean Validation 的 Spring Boot 起步依赖。
- `@NotBlank`、`@Size`：常用校验注解。
- `@Valid` + `BindingResult`：在 Controller 中触发校验并获取错误结果。
- `th:errors="*{fieldName}"`：Thymeleaf 显示字段错误。

### 修改步骤

#### 步骤 3.1：添加依赖

打开 [pom.xml](pom.xml)，在 `spring-boot-starter-security` 后添加：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

#### 步骤 3.2：给实体加注解

打开 [Book.java](src/main/java/com/example/bookmanagement/entity/Book.java)：

```java
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
```

打开 [RegisterRequest.java](src/main/java/com/example/bookmanagement/dto/RegisterRequest.java)：

```java
@NotBlank(message = "用户名不能为空")
@Size(min = 3, max = 20, message = "用户名长度必须在 3-20 个字符之间")
private String username;

@NotBlank(message = "密码不能为空")
@Size(min = 4, max = 20, message = "密码长度必须在 4-20 个字符之间")
private String password;

@NotBlank(message = "确认密码不能为空")
private String confirmPassword;
```

#### 步骤 3.3：Controller 使用 BindingResult

打开 [BookController.java](src/main/java/com/example/bookmanagement/controller/BookController.java)：

```java
@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/add")
public String add(@Valid @ModelAttribute("book") Book book, BindingResult bindingResult, Model model) {
    if (bindingResult.hasErrors()) {
        return "books/add";
    }
    bookService.addBook(book);
    return "redirect:/books";
}
```

编辑方法同理。

打开 [AuthController.java](src/main/java/com/example/bookmanagement/controller/AuthController.java)：

```java
@PostMapping("/register")
public String doRegister(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                         BindingResult bindingResult,
                         Model model) {
    if (bindingResult.hasErrors()) {
        return "register";
    }
    // ...
}
```

#### 步骤 3.4：模板显示错误

在 [books/add.html](src/main/resources/templates/books/add.html) 等表单中，每个输入框后添加：

```html
<div class="text-danger small mt-1" th:if="${#fields.hasErrors('name')}" th:errors="*{name}"></div>
```

### 验证方法

1. 打开添加图书页，清空图书名称后提交（可通过浏览器开发者工具移除 `required` 属性模拟绕过前端校验）。
2. 页面应返回表单，并显示“图书名称不能为空”。
3. 注册页输入 2 位用户名，应提示长度错误。

---

## 任务 4：全局异常处理与友好错误页

### 任务目标

统一处理异常，避免堆栈信息直接暴露给用户；为权限不足等常见错误提供友好提示。

### 关键知识点

- `@ControllerAdvice`：全局控制器增强，可集中处理异常。
- `@ExceptionHandler`：指定处理哪类异常。
- `AccessDeniedException`：Spring Security 权限不足异常。

### 修改步骤

#### 步骤 4.1：创建全局异常处理器

创建 [GlobalExceptionHandler.java](src/main/java/com/example/bookmanagement/exception/GlobalExceptionHandler.java)：

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model) {
        logger.warn("权限不足：", ex);
        model.addAttribute("errorTitle", "权限不足");
        model.addAttribute("errorMessage", "您没有权限执行该操作。");
        return "error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex, Model model) {
        logger.warn("业务参数异常：", ex);
        model.addAttribute("errorTitle", "请求错误");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        logger.error("系统异常：", ex);
        model.addAttribute("errorTitle", "系统错误");
        model.addAttribute("errorMessage", "系统发生异常，请稍后重试或联系管理员。");
        return "error";
    }
}
```

#### 步骤 4.2：创建错误页

创建 [error.html](src/main/resources/templates/error.html)：

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>出错了</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card shadow-sm">
                <div class="card-body text-center p-5">
                    <h2 class="card-title mb-3" th:text="${errorTitle}">错误</h2>
                    <p class="card-text text-muted" th:text="${errorMessage}"></p>
                    <a href="/" class="btn btn-primary mt-3">返回首页</a>
                    <a href="/books" class="btn btn-outline-primary mt-3 ms-2">图书列表</a>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
```

### 验证方法

1. 普通用户访问 `/books/add`，显示“权限不足”。
2. 访问不存在的图书 `/books/9999`，显示“图书不存在：9999”。
3. 制造一个运行时错误（如临时写一个除以 0），确认显示“系统错误”而非堆栈。

---

## 任务 5：图书搜索与分页

### 任务目标

让图书列表支持按名称或作者搜索，并支持分页。

### 关键知识点

- MyBatis 动态 SQL：`<script>`、`<where>`、`<if>`。
- `LIMIT` / `OFFSET`：MySQL 分页语法。
- Java `record`：简洁的数据载体。
- 分页参数边界校验。

### 修改步骤

#### 步骤 5.1：创建分页结果对象

创建 [PageResult.java](src/main/java/com/example/bookmanagement/dto/PageResult.java)：

```java
public record PageResult<T>(List<T> content, int page, int size, long total, int totalPages) { }
```

#### 步骤 5.2：Mapper 写动态 SQL

打开 [BookMapper.java](src/main/java/com/example/bookmanagement/mapper/BookMapper.java)：

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
```

#### 步骤 5.3：Service 计算分页

打开 [BookService.java](src/main/java/com/example/bookmanagement/service/BookService.java)：

```java
public PageResult<Book> findBooks(String keyword, int page, int size) {
    int offset = (page - 1) * size;
    List<Book> content = bookMapper.findByKeyword(keyword, offset, size);
    long total = bookMapper.countByKeyword(keyword);
    int totalPages = (int) Math.ceil((double) total / size);
    return new PageResult<>(content, page, size, total, totalPages);
}
```

#### 步骤 5.4：Controller 接收参数

打开 [BookController.java](src/main/java/com/example/bookmanagement/controller/BookController.java)，修改 `list` 方法：

```java
@GetMapping
public String list(@RequestParam(defaultValue = "") String keyword,
                   @RequestParam(defaultValue = "1") int page,
                   @RequestParam(defaultValue = "10") int size,
                   Model model) {
    if (page < 1) page = 1;
    if (size < 1 || size > 100) size = 10;
    var result = bookService.findBooks(keyword, page, size);
    model.addAttribute("books", result.content());
    model.addAttribute("page", result.page());
    model.addAttribute("size", result.size());
    model.addAttribute("total", result.total());
    model.addAttribute("totalPages", result.totalPages());
    model.addAttribute("keyword", keyword);
    return "books/list";
}
```

#### 步骤 5.5：列表页加搜索和分页

打开 [books/list.html](src/main/resources/templates/books/list.html)：

搜索表单：

```html
<form th:action="@{/books}" method="get" class="row g-2 mb-3">
    <div class="col-auto">
        <input type="text" class="form-control" name="keyword" th:value="${keyword}" placeholder="搜索图书名称或作者">
    </div>
    <div class="col-auto">
        <button type="submit" class="btn btn-outline-primary">搜索</button>
        <a th:href="@{/books}" class="btn btn-outline-secondary ms-1">重置</a>
    </div>
</form>
```

分页导航：

```html
<nav th:if="${totalPages > 1}" aria-label="图书分页" class="mt-3">
    <ul class="pagination justify-content-center">
        <li class="page-item" th:classappend="${page <= 1} ? 'disabled'">
            <a class="page-link" th:href="@{/books(keyword=${keyword}, page=${page - 1})}">上一页</a>
        </li>
        <li class="page-item disabled">
            <span class="page-link" th:text="${page} + ' / ' + ${totalPages}"></span>
        </li>
        <li class="page-item" th:classappend="${page >= totalPages} ? 'disabled'">
            <a class="page-link" th:href="@{/books(keyword=${keyword}, page=${page + 1})}">下一页</a>
        </li>
    </ul>
</nav>
```

### 验证方法

1. 在搜索框输入关键词，列表只显示匹配的图书。
2. 访问 `http://localhost:8080/books?size=1`，确认分页导航出现。
3. 点击“下一页”，URL 中 page 参数变化，列表显示不同记录。

---

## 任务 6：单元测试

### 任务目标

为新增的校验规则和分页逻辑编写测试。

### 关键知识点

- `Validation.buildDefaultValidatorFactory().getValidator()`：获取校验器。
- `@ExtendWith(MockitoExtension.class)`：启用 Mockito。
- `@Mock` 和 `@InjectMocks`：模拟依赖并注入被测对象。

### 修改步骤

#### 步骤 6.1：校验测试

创建 [BookValidationTest.java](src/test/java/com/example/bookmanagement/BookValidationTest.java)：

```java
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
        assertFalse(validator.validate(book).isEmpty());
    }
}
```

#### 步骤 6.2：Service 测试

创建 [BookServiceTest.java](src/test/java/com/example/bookmanagement/BookServiceTest.java)：

```java
@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock private BookMapper bookMapper;
    @InjectMocks private BookService bookService;

    @Test
    void findBooks_shouldReturnPagedResult() {
        when(bookMapper.findByKeyword(anyString(), anyInt(), anyInt())).thenReturn(List.of(new Book()));
        when(bookMapper.countByKeyword(anyString())).thenReturn(25L);
        PageResult<Book> result = bookService.findBooks("test", 2, 10);
        assertEquals(3, result.totalPages());
    }
}
```

### 验证方法

运行：

```bash
mvn clean test
```

确认所有测试通过。

---

## 任务 7：版本记录与提交

### 任务目标

学会使用 Git 记录项目演进，并撰写 CHANGELOG。

### 修改步骤

1. 创建 [CHANGELOG.md](CHANGELOG.md)，按模块记录本次所有改动。
2. 检查敏感信息：确保 `application.yml` 中没有生产密码。
3. 更新 [.gitignore](.gitignore)，排除不需要提交的文件（如验证截图）。
4. 按功能拆分提交：

```bash
git add ...
git commit -m "feat: 新增角色权限控制与完整图书 CRUD"
git add ...
git commit -m "feat: 新增服务端校验、全局异常处理与搜索分页"
git add ...
git commit -m "test & docs: 新增单元测试、CHANGELOG 与文档同步"
```

### 验证方法

```bash
git log --oneline -5
git status --short
mvn clean test
```

---

## 拓展任务（选做）

1. **借阅功能**：新增 `borrow_records` 表，实现借书/还书。
2. **图书分类管理**：将 `category` 改为独立表，支持增删改查。
3. **操作日志**：使用 AOP 记录管理员的关键操作。
4. **密码强度提示**：前端实时提示密码复杂度。

---

## 评价标准

| 等级 | 标准 |
|------|------|
| 优秀 | 独立完成全部 7 个任务，能解释原理，测试通过，文档清晰 |
| 良好 | 在少量提示下完成全部任务，能说明主要改动点 |
| 合格 | 完成主要任务，代码能运行，但解释不够完整 |
| 待改进 | 无法独立运行项目，或主要功能未实现 |
