# JavaWeb 实训任务书 —— 图书管理系统改造篇

## 第 1 章 文档信息

| 项目 | 内容 |
| --- | --- |
| 文档名称 | JavaWeb 实训任务书 —— 图书管理系统改造篇 |
| 版本号 | v2.0 |
| 发布日期 | 2026-06-22 |
| 适用课程 | JavaWeb 程序设计 / Spring Boot 实训 |
| 适用对象 | 零基础或刚接触 Web 开发的学习者 |
| 建议课时 | 16~20 课时（含讲解、实操、验收） |
| 配套资源 | [JavaWeb实训指导书.md](JavaWeb实训指导书.md)、[README.md](README.md)、[部署文档.md](部署文档.md)、[CHANGELOG.md](CHANGELOG.md)、[book-management.sql](book-management.sql) |
| 技术栈 | Spring Boot 3.2、Spring Security 6、Thymeleaf、MyBatis、MySQL 8.0、Bootstrap 5、Maven |

## 第 2 章 修订记录

| 版本 | 日期 | 修订人 | 修订内容 |
| --- | --- | --- | --- |
| v1.0 | 2026-06-22 | 教学组 | 初稿，覆盖角色权限、CRUD、校验、异常、分页、测试、Git 提交 7 个任务 |
| v2.0 | 2026-06-22 | 教学组 | 按正式实训文档规范重构，增加 AI Agent 使用引导、理解 checkpoint、反向验证、代码审查清单与提交验收规范 |

## 第 3 章 实训概述

### 3.1 实训背景

本项目为一个基于 Spring Boot 的图书管理系统教学演示程序。原始版本已实现用户注册登录、图书新增、列表展示、删除以及默认管理员账号 `admin/123456`。但作为一个真实可用的系统，它还缺少角色权限区分、图书编辑与详情、服务端数据校验、统一异常处理、搜索分页以及自动化测试等能力。

本次实训要求学习者在理解原始代码的基础上，逐步补齐上述能力，最终形成一个功能完整、结构清晰、可测试、可维护的 Web 应用。

### 3.2 实训目标

完成本实训后，学习者应能够：

#### 知识目标

- 理解 Spring Security 的方法级权限控制原理。
- 理解 Thymeleaf 模板与后端数据的绑定方式。
- 理解 MyBatis 注解方式下的基本 CRUD 与动态 SQL 写法。
- 理解 Jakarta Bean Validation 的服务端校验机制。
- 理解 `@ControllerAdvice` 全局异常处理的作用。
- 理解单元测试与 Mockito 的基本用法。

#### 能力目标

- 能够使用 AI Agent 辅助阅读、解释和修改代码。
- 能够按照任务步骤独立完成代码改造。
- 能够为每个改动提供端到端的手工验证。
- 能够使用 Git 按功能拆分提交并撰写 CHANGELOG。

#### 素养目标

- 养成“先理解、再提问、后实现”的 vibecoding 工作习惯。
- 养成代码审查、验证留痕、版本记录的工程意识。
- 树立安全意识：不硬编码密码、不泄露敏感信息。

### 3.3 任务总览

| 任务编号 | 任务名称 | 建议用时 | 核心产出 |
| --- | --- | --- | --- |
| T1 | 角色权限控制（RBAC） | 2 课时 | ADMIN/USER 权限区分、前后端权限一致 |
| T2 | 图书详情与编辑功能 | 2 课时 | 完整 CRUD、详情页、编辑页 |
| T3 | 服务端数据校验 | 2 课时 | 实体校验、Controller 校验、表单错误回显 |
| T4 | 全局异常处理与友好错误页 | 2 课时 | 统一异常处理器、错误页面 |
| T5 | 图书搜索与分页 | 3 课时 | 动态 SQL 搜索、分页结果对象、分页导航 |
| T6 | 单元测试 | 2 课时 | 校验测试、Service 分页测试 |
| T7 | 版本记录与 Git 提交 | 1 课时 | CHANGELOG、按功能拆分提交 |

### 3.4 最终交付物清单

- 可运行的图书管理系统源码。
- 通过 `mvn clean test` 的测试用例。
- 更新后的 [CHANGELOG.md](CHANGELOG.md)。
- 本任务书中要求的理解 checkpoint 记录与验证截图。
- 清晰的 Git 提交历史。

## 第 4 章 前置准备

### 4.1 环境要求

| 软件 | 版本要求 | 说明 |
| --- | --- | --- |
| JDK | 17 及以上 | 运行 Spring Boot 3.2 的最低要求 |
| MySQL | 8.0 | 存储用户和图书数据 |
| IntelliJ IDEA | 2022 及以上 | 推荐 IDE，社区版即可 |
| Maven | 3.8 及以上 | 构建工具，也可使用 IDEA 自带的 Maven |
| 浏览器 | Chrome / Edge / Firefox | 用于手工验证页面功能 |

### 4.2 项目导入与运行

1. 将项目导入 IntelliJ IDEA（File → Open → 选择项目根目录）。
2. 打开 [src/main/resources/application.yml](src/main/resources/application.yml)，确认数据库连接信息。
3. 登录本地 MySQL，执行初始化脚本：

   ```bash
   mysql -uroot -p123456 < book-management.sql
   ```

4. 若本地 MySQL root 密码不是 `123456`，请修改 `application.yml` 中的 `password` 字段，但**不要将该修改提交到 Git**。
5. 在 IDEA 中打开 Maven 工具窗，执行：

   ```bash
   mvn clean package -DskipTests
   ```

6. 运行：

   ```bash
   java -jar target/book-management-0.0.1-SNAPSHOT.jar
   ```

7. 浏览器访问 `http://localhost:8080`，使用 `admin/123456` 登录，确认原始功能正常。

### 4.3 前置知识自查表

在开始任务前，请确认你已理解以下基础概念。若有疑问，可先阅读 [JavaWeb实训指导书.md](JavaWeb实训指导书.md) 或使用 AI Agent 进行解释。

- [ ] 知道浏览器、服务器、数据库三者如何交互。
- [ ] 知道 HTML 表单、`input`、`button`、`a` 标签的基本作用。
- [ ] 知道 Java 类、方法、注解的基本含义。
- [ ] 知道什么是数据库表、字段、主键。
- [ ] 知道如何运行 `mvn` 命令。
- [ ] 知道 `git add`、`git commit`、`git log` 的基本作用。

## 第 5 章 评分规则

### 5.1 评分维度

| 维度 | 权重 | 说明 |
| --- | --- | --- |
| 功能完成度 | 35% | 7 个核心任务是否全部完成并通过验证 |
| 代码可解释性 | 20% | 能否清晰解释自己代码中的关键修改 |
| 验证完整性 | 20% | 是否按任务书完成正常路径与反向路径验证，是否有截图或记录 |
| 代码质量 | 15% | 命名规范、结构清晰、无重复、无未使用导入、无明显安全隐患 |
| 文档与 Git | 10% | CHANGELOG 是否完整、提交历史是否按功能拆分、是否有个人理解记录 |

### 5.2 等级标准

| 等级 | 标准 |
| --- | --- |
| 优秀 | 7 个任务全部完成，能独立解释任意 3 处关键代码，所有验证通过，代码结构清晰，Git 提交规范 |
| 良好 | 7 个任务基本完成，能在少量提示下解释主要改动，验证记录较完整 |
| 合格 | 主要任务完成，代码可运行，但部分验证缺失或解释不够完整 |
| 待改进 | 无法独立运行项目，或主要功能未实现，或无法解释代码含义 |

### 5.3 扣分项

- 直接复制 AI 生成的代码但无法解释其含义：扣 10~20 分。
- `application.yml` 中提交个人真实数据库密码：扣 10 分。
- 未运行验证即声称完成：扣 10 分。
- Git 提交历史混乱（如所有改动一次性提交）：扣 5~10 分。
- 代码中存在明显安全隐患（如 SQL 拼接、XSS 未转义）：扣 10 分。

## 第 6 章 AI Agent 使用规范

### 6.1 使用原则

AI Agent（如 Claude Code）是本次实训的辅助工具，不是替代你思考的黑箱。使用时应遵循以下原则：

1. **先理解，再提问**：在让 AI 修改代码前，先自己阅读相关文件，明确要改什么。
2. **小步快跑**：每次只让 AI 协助完成一个最小步骤，不要一次性要求生成整个任务的全部代码。
3. **追问原因**：对 AI 给出的代码，必须要求解释“为什么这样改”。
4. **自己验证**：AI 给出的代码必须手动运行验证，不能假设它一定正确。
5. **记录对话**：保留关键 AI 对话记录，作为理解 checkpoint 的佐证。

### 6.2 推荐提问流程

```text
第 1 步：描述上下文
“我正在完成图书管理系统的【任务名称】。当前相关文件有：
- Controller: src/main/java/com/example/bookmanagement/controller/BookController.java
- Service: ...
- Mapper: ...
- 页面: ...

第 2 步：说明目标
我想实现：【具体行为，例如 ADMIN 能删除图书，USER 不能】。

第 3 步：提出请求
请告诉我：
1. 需要修改哪些文件？
2. 每处改动的原因是什么？
3. 改完后我该如何验证？
4. 可能出现什么错误，如何排查？
```

### 6.3 标准提示词模板

以下是每个任务都可复用的提示词框架，请根据实际情况填入文件路径和具体目标。

**模板 A：解释代码**

```text
请解释【文件名】中【方法名/代码块】的作用，以及它在整个请求处理流程中处于哪个位置。
请用中文回答，并给出文件路径和行号。
```

**模板 B：实现功能**

```text
我想实现【功能目标】。
当前相关文件：
- 【文件 1】
- 【文件 2】

请按以下步骤帮我：
1. 分析当前代码状态；
2. 列出需要新增或修改的文件；
3. 给出每处改动的代码和原因；
4. 告诉我验证方法。
```

**模板 C：排查错误**

```text
我运行【命令/操作】时遇到以下错误：
【粘贴完整错误信息】

相关代码：
【粘贴相关代码片段】

请帮我分析可能的原因和排查步骤。
```

### 6.4 禁止行为

- 不要直接复制本任务书中的代码片段给 AI，然后让 AI “帮我写一样的”。
- 不要一次性要求 AI 生成整个 `BookController.java` 或整个 `books/list.html` 的重写版本。
- 不要在未理解代码含义的情况下直接应用 AI 的修改建议。
- 不要向 AI 发送真实数据库密码、服务器地址等敏感信息。

## 第 7 章 任务详情

---

### 任务 T1：角色权限控制（RBAC）

#### 任务信息（T1）

| 项目 | 内容 |
| --- | --- |
| 任务编码 | T1 |
| 任务名称 | 角色权限控制（RBAC） |
| 建议用时 | 2 课时 |
| 前置任务 | 环境准备完成，原始项目可运行 |

#### 学习目标（T1）

完成本任务后，学习者应能够：

- 开启 Spring Boot 的方法级安全注解支持。
- 使用 `@PreAuthorize` 保护后端管理端点。
- 使用 Thymeleaf Spring Security 扩展按角色控制前端元素显示。
- 解释“后端权限控制是底线，前端隐藏只是体验优化”这一设计原则。

#### 前置状态（T1）

原始项目的 `users` 表已有 `role` 字段，`UserDetailsServiceImpl` 会把角色写入 Spring Security 上下文，但 `SecurityConfig` 没有开启方法级权限控制，因此所有登录用户都能执行新增、删除、编辑等管理操作。

#### 涉及文件（T1）

- [src/main/java/com/example/bookmanagement/config/SecurityConfig.java](src/main/java/com/example/bookmanagement/config/SecurityConfig.java)
- [src/main/java/com/example/bookmanagement/controller/BookController.java](src/main/java/com/example/bookmanagement/controller/BookController.java)
- [src/main/resources/templates/books/list.html](src/main/resources/templates/books/list.html)
- [src/main/resources/templates/books/add.html](src/main/resources/templates/books/add.html)
- [src/main/resources/templates/books/detail.html](src/main/resources/templates/books/detail.html)
- [src/main/resources/templates/books/edit.html](src/main/resources/templates/books/edit.html)

#### 核心概念（T1）

- `@EnableMethodSecurity(prePostEnabled = true)`：在配置类上开启方法级安全注解，使 `@PreAuthorize` 生效。
- `@PreAuthorize("hasRole('ADMIN')")`：在方法调用前进行权限判断，只有具备 `ADMIN` 角色的用户才能执行。
- `sec:authorize="hasRole('ADMIN')"`：Thymeleaf 中用于按角色条件渲染页面元素。

#### 理解 Checkpoint（T1）

在向 AI 提问前，请先自己尝试回答以下问题，并将答案记录在个人学习笔记中：

1. `SecurityConfig` 中 `SecurityFilterChain` 的 `authorizeHttpRequests` 与 `@PreAuthorize` 有什么区别？
2. 如果只在 Thymeleaf 中隐藏“删除”按钮，但后端没有加权限控制，会产生什么安全风险？
3. `hasRole('ADMIN')` 中的 `ADMIN` 与数据库 `users.role` 字段的值是否完全一致？为什么？

#### 操作步骤（T1）

##### 步骤 T1.1：开启方法级安全

**操作**：打开 `SecurityConfig.java`，在类上添加 `@EnableMethodSecurity(prePostEnabled = true)`。

**预期结果**：编译通过，项目仍可正常启动。

**AI 提示词模板**：

```text
请解释 SecurityConfig.java 中 @EnableMethodSecurity(prePostEnabled = true) 的作用，
以及不添加这个注解时 @PreAuthorize 为什么不会生效。
```

**关键代码参考**：

```java
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    // ...
}
```

##### 步骤 T1.2：保护管理端点

**操作**：打开 `BookController.java`，在新增、编辑、删除相关方法上添加 `@PreAuthorize("hasRole('ADMIN')")`。

**预期结果**：普通用户直接访问 `/books/add`、`/books/delete/{id}`、`/books/{id}/edit` 等 URL 时会被拒绝。

**AI 提示词模板**：

```text
我想让 BookController 中只有 ADMIN 角色能执行新增、编辑、删除操作。
请告诉我应该在哪些方法上加 @PreAuthorize，并解释为什么 list 和 detail 方法不需要加。
```

**关键代码参考**：

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

##### 步骤 T1.3：前端隐藏无权限按钮

**操作**：在 `books/list.html`、`books/add.html`、`books/detail.html`、`books/edit.html` 中，使用 `sec:authorize="hasRole('ADMIN')"` 包裹“添加图书”“编辑”“删除”等入口。

**预期结果**：普通用户登录后，页面中看不到任何管理操作入口。

**AI 提示词模板**：

```text
我已在 BookController 上加好 @PreAuthorize，现在想在 Thymeleaf 页面中按角色隐藏按钮。
请解释 sec:authorize="hasRole('ADMIN')" 的用法，并告诉我 list.html 中哪些元素需要包裹它。
```

**关键代码参考**：

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

#### 验证清单（T1）

- [ ] 使用 `admin/123456` 登录，确认能看到“添加图书”“编辑”“删除”按钮。
- [ ] 注册一个普通用户并登录，确认只能看到“详情”按钮。
- [ ] 普通用户直接访问 `http://localhost:8080/books/add`，应看到“权限不足”或类似提示，而不是系统堆栈。
- [ ] 普通用户直接访问 `http://localhost:8080/books/delete/1`，应被阻止。
- [ ] 普通用户直接访问 `http://localhost:8080/books/1/edit`，应被阻止。

#### 提交检查点（T1）

- 代码可编译并运行。
- 完成上述验证清单，至少保留 1 张普通用户访问 `/books/add` 被阻止的截图。
- 在个人学习笔记中记录理解 checkpoint 的答案。

#### 常见问题（T1）

**Q1：加了 `@PreAuthorize` 后，普通用户访问报错页面显示“系统错误”而不是“权限不足”。**

A：说明全局异常处理还未实现（将在 T4 实现）。在 T1 阶段，只要确认普通用户无法成功执行管理操作即可。

**Q2：Thymeleaf 中 `sec:authorize` 不生效，所有按钮仍然显示。**

A：确认 `pom.xml` 中已引入 `thymeleaf-extras-springsecurity6`，且页面根标签中声明了 `xmlns:sec="http://www.thymeleaf.org/extras/spring-security`。

---

### 任务 T2：图书详情与编辑功能

#### 任务信息（T2）

| 项目 | 内容 |
| --- | --- |
| 任务编码 | T2 |
| 任务名称 | 图书详情与编辑功能 |
| 建议用时 | 2 课时 |
| 前置任务 | T1 完成，权限控制已生效 |

#### 学习目标（T2）

完成本任务后，学习者应能够：

- 使用 `@PathVariable` 从 URL 中获取资源 ID。
- 在 MyBatis 中编写 UPDATE 语句。
- 设计详情页和编辑页，并实现预填充表单。
- 理解 RESTful URL 设计的基本思想。

#### 前置状态（T2）

系统当前只有新增、列表、删除功能，缺少查看图书详情和修改图书信息的能力。

#### 涉及文件（T2）

- [src/main/java/com/example/bookmanagement/mapper/BookMapper.java](src/main/java/com/example/bookmanagement/mapper/BookMapper.java)
- [src/main/java/com/example/bookmanagement/service/BookService.java](src/main/java/com/example/bookmanagement/service/BookService.java)
- [src/main/java/com/example/bookmanagement/controller/BookController.java](src/main/java/com/example/bookmanagement/controller/BookController.java)
- [src/main/resources/templates/books/detail.html](src/main/resources/templates/books/detail.html)（新增）
- [src/main/resources/templates/books/edit.html](src/main/resources/templates/books/edit.html)（新增）
- [src/main/resources/templates/books/list.html](src/main/resources/templates/books/list.html)

#### 核心概念（T2）

- `@GetMapping("/{id}")`：定义 RESTful 风格的 URL 参数。
- `@PathVariable Long id`：从 URL 路径中提取变量值。
- `bookService.findById(id)`：根据 ID 查询单个对象。
- `bookMapper.update(book)`：更新数据库记录。

#### 理解 Checkpoint（T2）

1. 为什么详情页的 URL 设计成 `/books/{id}` 而不是 `/books/detail?id=1`？各有什么优缺点？
2. 编辑表单为什么要预填充现有数据？如果直接提交空表单会发生什么？
3. `Book` 实体中的 `id` 在编辑时如何与 URL 中的 `id` 对应？

#### 操作步骤（T2）

##### 步骤 T2.1：Mapper 新增 update 方法

**操作**：打开 `BookMapper.java`，新增 `update` 方法。

**预期结果**：Service 层可以调用该方法更新图书。

**AI 提示词模板**：

```text
我想在 BookMapper 中新增一个根据 id 更新图书全部字段的方法。
请给出 MyBatis @Update 注解的写法，并解释 #{name} 这种占位符的作用。
```

**关键代码参考**：

```java
@Update("UPDATE books SET name = #{name}, category = #{category}, author = #{author}, publisher = #{publisher}, version = #{version} WHERE id = #{id}")
int update(Book book);
```

##### 步骤 T2.2：Service 新增 updateBook 方法

**操作**：打开 `BookService.java`，新增 `updateBook` 方法。

**预期结果**：Controller 可以通过 Service 调用更新逻辑。

**AI 提示词模板**：

```text
请解释 Service 层的作用，为什么 Controller 不直接调用 BookMapper.update，而是要通过 BookService.updateBook？
```

**关键代码参考**：

```java
public void updateBook(Book book) {
    bookMapper.update(book);
}
```

##### 步骤 T2.3：Controller 新增端点

**操作**：打开 `BookController.java`，新增详情、编辑表单、保存编辑三个端点。

**预期结果**：

- 访问 `/books/1` 显示 ID 为 1 的图书详情。
- 访问 `/books/1/edit` 显示预填充的编辑表单（仅 ADMIN）。
- 提交编辑表单后保存并跳转到列表页（仅 ADMIN）。
- 访问不存在的图书时抛出 `IllegalArgumentException`。

**AI 提示词模板**：

```text
我想在 BookController 中新增图书详情页、编辑页和保存编辑功能。
请告诉我需要新增哪些方法，URL 如何设计，以及如何在编辑方法中把 URL 的 id 设置到 book 对象上。
```

**关键代码参考**：

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

##### 步骤 T2.4：新增详情页

**操作**：创建 `books/detail.html`。

**预期结果**：详情页以卡片形式展示图书全部字段，ADMIN 可见“编辑”按钮。

**AI 提示词模板**：

```text
请帮我创建一个图书详情页 detail.html，要求：
1. 使用 Bootstrap 卡片展示图书名称、作者、出版社、版本、类别；
2. ADMIN 角色显示“编辑”按钮；
3. 提供“返回列表”链接。
```

**关键代码参考**：

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
<head>
    <meta charset="UTF-8">
    <title>图书详情</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
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

##### 步骤 T2.5：新增编辑页

**操作**：创建 `books/edit.html`，结构与 `books/add.html` 类似，但表单的 `th:action` 指向 `@{/books/{id}/edit(id=${book.id})}`，输入框使用 `th:field="*{name}"` 预填充数据。

**预期结果**：点击“编辑”后，表单中显示当前图书的原有数据。

**AI 提示词模板**：

```text
请帮我基于 add.html 创建一个 edit.html，要求：
1. 表单 action 指向 /books/{id}/edit；
2. 每个输入框预填充当前图书数据；
3. 保留与 add.html 一致的样式。
```

#### 验证清单（T2）

- [ ] 在图书列表点击“详情”，能正确显示图书信息。
- [ ] 点击“编辑”，进入预填充的编辑表单。
- [ ] 修改图书名称并保存，列表中对应记录更新。
- [ ] 访问不存在的图书详情 `/books/9999`，应提示“图书不存在：9999”。
- [ ] 普通用户尝试访问 `/books/1/edit`，应被阻止。

#### 提交检查点（T2）

- 新增 `books/detail.html` 和 `books/edit.html`。
- 更新 `BookMapper`、`BookService`、`BookController`、`books/list.html`。
- 保留编辑前后的列表截图各 1 张。

#### 常见问题（T2）

**Q1：编辑页提交后没有更新数据，而是新增了一条记录。**

A：检查编辑表单的 `th:action` 是否指向 `/books/{id}/edit`，以及 Controller 的 `update` 方法是否通过 `book.setId(id)` 设置了 ID。

**Q2：详情页显示空白，控制台提示 `book` 为 null。**

A：检查 Controller 的 `detail` 方法是否正确将 `book` 放入 `model`，以及模板中是否使用了正确的属性名。

---

### 任务 T3：服务端数据校验

#### 任务信息（T3）

| 项目 | 内容 |
| --- | --- |
| 任务编码 | T3 |
| 任务名称 | 服务端数据校验 |
| 建议用时 | 2 课时 |
| 前置任务 | T2 完成，图书 CRUD 已补齐 |

#### 学习目标（T3）

完成本任务后，学习者应能够：

- 引入 Jakarta Bean Validation 依赖。
- 使用 `@NotBlank`、`@Size` 等注解对实体字段进行约束。
- 在 Controller 中使用 `@Valid` 和 `BindingResult` 触发校验并处理错误。
- 在 Thymeleaf 表单中回显字段级错误信息。

#### 前置状态（T3）

系统当前仅依赖前端 HTML5 的 `required` 属性进行校验，后端无校验。绕过前端即可提交非法数据。

#### 涉及文件（T3）

- [pom.xml](pom.xml)
- [src/main/java/com/example/bookmanagement/entity/Book.java](src/main/java/com/example/bookmanagement/entity/Book.java)
- [src/main/java/com/example/bookmanagement/dto/RegisterRequest.java](src/main/java/com/example/bookmanagement/dto/RegisterRequest.java)
- [src/main/java/com/example/bookmanagement/controller/AuthController.java](src/main/java/com/example/bookmanagement/controller/AuthController.java)
- [src/main/java/com/example/bookmanagement/controller/BookController.java](src/main/java/com/example/bookmanagement/controller/BookController.java)
- [src/main/resources/templates/books/add.html](src/main/resources/templates/books/add.html)
- [src/main/resources/templates/books/edit.html](src/main/resources/templates/books/edit.html)
- [src/main/resources/templates/register.html](src/main/resources/templates/register.html)

#### 核心概念（T3）

- `spring-boot-starter-validation`：Jakarta Bean Validation 的 Spring Boot 起步依赖。
- `@NotBlank`、`@Size`：常用校验注解。
- `@Valid` + `BindingResult`：在 Controller 中触发校验并获取错误结果。
- `th:errors="*{fieldName}"`：Thymeleaf 显示字段错误。

#### 理解 Checkpoint（T3）

1. 为什么前端已经有 `required`，后端还需要校验？
2. `@Valid` 和 `BindingResult` 必须一起出现吗？如果只有 `@Valid` 没有 `BindingResult` 会怎样？
3. 校验注解中的 `message` 属性有什么作用？

#### 操作步骤（T3）

##### 步骤 T3.1：添加依赖

**操作**：打开 `pom.xml`，在 `spring-boot-starter-security` 后添加 `spring-boot-starter-validation`。

**预期结果**：Maven 重新导入后，`javax.validation` 或 `jakarta.validation` 相关类可用。

**AI 提示词模板**：

```text
我想在 Spring Boot 项目中使用 @Valid 和 @NotBlank 进行服务端校验，
请告诉我需要在 pom.xml 中添加什么依赖，并说明该依赖的作用。
```

**关键代码参考**：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

##### 步骤 T3.2：给实体加注解

**操作**：打开 `Book.java` 和 `RegisterRequest.java`，为字段添加校验注解。

**预期结果**：非法对象在校验时会产生约束违规信息。

**AI 提示词模板**：

```text
请解释 @NotBlank 和 @Size 的区别，并帮我为 Book 实体的 name、category、author、publisher、version 字段添加合适的校验注解。
```

**关键代码参考**：

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

##### 步骤 T3.3：Controller 使用 BindingResult

**操作**：打开 `BookController.java` 和 `AuthController.java`，在需要校验的参数前加 `@Valid`，并添加 `BindingResult` 参数处理错误。

**预期结果**：校验失败时返回原表单页面，不执行保存逻辑。

**AI 提示词模板**：

```text
我已在 Book 实体上加好校验注解，现在想在 BookController 的 add 方法中触发校验。
请告诉我如何使用 @Valid 和 BindingResult，并解释如果 bindingResult.hasErrors() 为 true 时为什么直接返回 "books/add"。
```

**关键代码参考**：

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

##### 步骤 T3.4：模板显示错误

**操作**：在 `books/add.html`、`books/edit.html`、`register.html` 的每个输入框后添加错误显示区域。

**预期结果**：提交非法数据后，页面在原表单位置显示具体错误信息。

**AI 提示词模板**：

```text
我已在 Controller 中使用 BindingResult，现在想在 Thymeleaf 表单中显示每个字段的校验错误。
请给出显示 name 字段错误的 HTML 代码，并解释 th:if、th:errors 的作用。
```

**关键代码参考**：

```html
<div class="text-danger small mt-1" th:if="${#fields.hasErrors('name')}" th:errors="*{name}"></div>
```

#### 验证清单（T3）

- [ ] 打开添加图书页，清空图书名称后提交（可通过浏览器开发者工具移除 `required` 属性模拟绕过前端校验），页面返回表单并显示“图书名称不能为空”。
- [ ] 输入超过 100 个字符的图书名称提交，显示长度错误。
- [ ] 编辑页同样能回显校验错误。
- [ ] 注册页输入 2 位用户名，提示长度错误。
- [ ] 正常数据提交不受影响。

#### 提交检查点（T3）

- `pom.xml` 新增依赖。
- 实体类和 Controller 已加校验逻辑。
- 表单页面已加错误显示。
- 保留 1 张校验错误回显的截图。

#### 常见问题（T3）

**Q1：校验不生效，非法数据仍然入库。**

A：检查参数前是否有 `@Valid`，以及方法参数中是否包含 `BindingResult`。

**Q2：错误信息没有显示在页面上。**

A：检查模板中 `th:errors="*{name}"` 的字段名是否与 `Book` 实体属性名一致，以及 `th:if` 条件是否正确。

---

### 任务 T4：全局异常处理与友好错误页

#### 任务信息（T4）

| 项目 | 内容 |
| --- | --- |
| 任务编码 | T4 |
| 任务名称 | 全局异常处理与友好错误页 |
| 建议用时 | 2 课时 |
| 前置任务 | T3 完成，服务端校验已生效 |

#### 学习目标（T4）

完成本任务后，学习者应能够：

- 使用 `@ControllerAdvice` 和 `@ExceptionHandler` 集中处理异常。
- 为权限不足、业务参数错误、系统异常提供不同的错误提示。
- 理解异常处理中日志记录的重要性。

#### 前置状态（T4）

系统当前未统一处理异常。普通用户访问 `/books/add` 时可能直接看到 Whitelabel 错误页或堆栈信息；访问不存在的图书时也会抛出未处理的异常。

#### 涉及文件（T4）

- [src/main/java/com/example/bookmanagement/exception/GlobalExceptionHandler.java](src/main/java/com/example/bookmanagement/exception/GlobalExceptionHandler.java)（新增）
- [src/main/resources/templates/error.html](src/main/resources/templates/error.html)（新增）

#### 核心概念（T4）

- `@ControllerAdvice`：全局控制器增强，可集中处理异常。
- `@ExceptionHandler`：指定处理哪类异常。
- `AccessDeniedException`：Spring Security 权限不足异常。
- `IllegalArgumentException`：业务参数错误异常。

#### 理解 Checkpoint（T4）

1. `@ControllerAdvice` 与单个 Controller 中的 `@ExceptionHandler` 有什么区别？
2. 为什么权限不足和系统异常要分别处理？
3. 异常处理中为什么要记录日志？

#### 操作步骤（T4）

##### 步骤 T4.1：创建全局异常处理器

**操作**：创建 `GlobalExceptionHandler.java`。

**预期结果**：发生对应异常时，自动跳转到 `error.html` 并显示友好提示。

**AI 提示词模板**：

```text
我想统一处理以下异常：
1. AccessDeniedException —— 显示“权限不足”；
2. IllegalArgumentException —— 显示异常消息；
3. 其他 Exception —— 显示“系统错误”。

请帮我写一个 @ControllerAdvice 类，并解释每个 @ExceptionHandler 的匹配规则。
```

**关键代码参考**：

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

##### 步骤 T4.2：创建错误页

**操作**：创建 `error.html`。

**预期结果**：错误页显示 `errorTitle` 和 `errorMessage`，并提供返回首页和图书列表的链接。

**AI 提示词模板**：

```text
请帮我创建一个 Bootstrap 风格的错误页 error.html，要求：
1. 显示 errorTitle 和 errorMessage；
2. 提供“返回首页”和“图书列表”两个链接；
3. 居中显示，简洁美观。
```

**关键代码参考**：

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

#### 验证清单（T4）

- [ ] 普通用户访问 `/books/add`，显示“权限不足”页面，而不是堆栈或 Whitelabel 页。
- [ ] 访问不存在的图书 `/books/9999`，显示“图书不存在：9999”。
- [ ] 临时在代码中制造一个运行时错误（如 `int x = 1 / 0`），确认显示“系统错误”而非堆栈。
- [ ] 确认日志文件中记录了异常信息。

#### 提交检查点（T4）

- 新增 `GlobalExceptionHandler.java` 和 `error.html`。
- 保留 1 张权限不足错误页的截图。
- 保留 1 张“图书不存在”错误页的截图。

#### 常见问题（T4）

**Q1：AccessDeniedException 没有被捕获，仍然显示 Whitelabel 页。**

A：确认引入的是 `org.springframework.security.access.AccessDeniedException`，并确认 `@ControllerAdvice` 类被 Spring 扫描到（位于 `com.example.bookmanagement` 包或其子包下）。

**Q2：异常信息没有传递到错误页。**

A：检查 `model.addAttribute("errorTitle", ...)` 中的属性名是否与 `error.html` 中的 `th:text="${errorTitle}"` 一致。

---

### 任务 T5：图书搜索与分页

#### 任务信息（T5）

| 项目 | 内容 |
| --- | --- |
| 任务编码 | T5 |
| 任务名称 | 图书搜索与分页 |
| 建议用时 | 3 课时 |
| 前置任务 | T4 完成，异常处理已生效 |

#### 学习目标（T5）

完成本任务后，学习者应能够：

- 使用 Java `record` 定义简洁的数据载体。
- 使用 MyBatis 动态 SQL 实现条件查询。
- 使用 `LIMIT`/`OFFSET` 实现 MySQL 分页。
- 在 Controller 中处理分页参数并做边界校验。
- 在 Thymeleaf 中渲染分页导航并保留搜索条件。

#### 前置状态（T5）

系统当前一次性加载全部图书，无搜索能力。当图书数量较多时，页面冗长且性能下降。

#### 涉及文件（T5）

- [src/main/java/com/example/bookmanagement/dto/PageResult.java](src/main/java/com/example/bookmanagement/dto/PageResult.java)（新增）
- [src/main/java/com/example/bookmanagement/mapper/BookMapper.java](src/main/java/com/example/bookmanagement/mapper/BookMapper.java)
- [src/main/java/com/example/bookmanagement/service/BookService.java](src/main/java/com/example/bookmanagement/service/BookService.java)
- [src/main/java/com/example/bookmanagement/controller/BookController.java](src/main/java/com/example/bookmanagement/controller/BookController.java)
- [src/main/resources/templates/books/list.html](src/main/resources/templates/books/list.html)

#### 核心概念（T5）

- MyBatis 动态 SQL：`<script>`、`<where>`、`<if>`。
- `LIMIT` / `OFFSET`：MySQL 分页语法。
- Java `record`：简洁的数据载体。
- 分页参数边界校验。

#### 理解 Checkpoint（T5）

1. 为什么要同时写 `findByKeyword` 和 `countByKeyword` 两个方法？
2. `LIMIT` 和 `OFFSET` 的计算公式是什么？如果 `page=2, size=10`，`OFFSET` 应该是多少？
3. 为什么分页导航的链接中要保留 `keyword` 参数？

#### 操作步骤（T5）

##### 步骤 T5.1：创建分页结果对象

**操作**：创建 `PageResult.java`。

**预期结果**：Service 层可返回包含内容、当前页、每页大小、总数、总页数的分页对象。

**AI 提示词模板**：

```text
我想定义一个分页结果对象 PageResult，包含内容列表、当前页、每页大小、总数、总页数。
请解释为什么使用 Java record，以及它与普通类的区别。
```

**关键代码参考**：

```java
public record PageResult<T>(List<T> content, int page, int size, long total, int totalPages) { }
```

##### 步骤 T5.2：Mapper 写动态 SQL

**操作**：打开 `BookMapper.java`，新增 `findByKeyword` 和 `countByKeyword` 方法。

**预期结果**：无关键字时返回全部图书；有关键字时按名称或作者模糊匹配。

**AI 提示词模板**：

```text
我想在 BookMapper 中实现按关键字搜索图书，并按创建时间倒序分页返回。
请使用 MyBatis 动态 SQL 写法（@Select + <script>），解释 <where> 和 <if> 的作用。
```

**关键代码参考**：

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

##### 步骤 T5.3：Service 计算分页

**操作**：打开 `BookService.java`，新增 `findBooks` 方法。

**预期结果**：传入 `keyword`、`page`、`size`，返回计算好的 `PageResult`。

**AI 提示词模板**：

```text
请帮我写 BookService 中的 findBooks 方法，要求：
1. 根据 page 和 size 计算 offset；
2. 查询数据和总数；
3. 计算总页数；
4. 返回 PageResult。
```

**关键代码参考**：

```java
public PageResult<Book> findBooks(String keyword, int page, int size) {
    int offset = (page - 1) * size;
    List<Book> content = bookMapper.findByKeyword(keyword, offset, size);
    long total = bookMapper.countByKeyword(keyword);
    int totalPages = (int) Math.ceil((double) total / size);
    return new PageResult<>(content, page, size, total, totalPages);
}
```

##### 步骤 T5.4：Controller 接收参数

**操作**：打开 `BookController.java`，修改 `list` 方法接收 `keyword`、`page`、`size` 参数，并做边界校验。

**预期结果**：访问 `/books?keyword=Java&page=1&size=10` 能正确返回搜索结果。

**AI 提示词模板**：

```text
我想让 BookController 的 list 方法支持搜索关键字 keyword、页码 page、每页大小 size 三个参数。
请告诉我如何使用 @RequestParam 设置默认值，并说明为什么要对 page 和 size 做边界校验。
```

**关键代码参考**：

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

##### 步骤 T5.5：列表页加搜索和分页

**操作**：打开 `books/list.html`，在表格上方添加搜索表单，在表格下方添加分页导航。

**预期结果**：

- 搜索框输入关键字后，列表只显示匹配结果。
- 当总数超过每页大小时，显示“上一页/下一页”导航。
- 翻页时 URL 中保留当前 `keyword`。

**AI 提示词模板**：

```text
请在 books/list.html 中：
1. 添加一个搜索表单，包含 keyword 输入框和搜索按钮；
2. 在表格下方添加 Bootstrap 分页导航，包含上一页、当前页/总页数、下一页；
3. 确保翻页链接保留当前 keyword 参数。
```

**关键代码参考**：

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

#### 验证清单（T5）

- [ ] 在搜索框输入关键词，列表只显示匹配的图书。
- [ ] 输入一个不存在的关键词，列表为空，分页导航不显示。
- [ ] 访问 `http://localhost:8080/books?size=1`，确认分页导航出现。
- [ ] 点击“下一页”，URL 中 `page` 参数变化，列表显示不同记录。
- [ ] 在第二页再次搜索，确认页码重置为 1。
- [ ] 访问 `http://localhost:8080/books?page=0&size=1000`，确认被边界校验修正。

#### 提交检查点（T5）

- 新增 `PageResult.java`。
- 更新 `BookMapper`、`BookService`、`BookController`、`books/list.html`。
- 保留 1 张搜索结果的截图和 1 张分页导航的截图。

#### 常见问题（T5）

**Q1：搜索无结果，但数据库中明明有匹配数据。**

A：检查 MyBatis 动态 SQL 中的 `keyword` 参数名是否与 `@Param("keyword")` 一致，以及 SQL 是否正确拼接了 `%` 通配符。

**Q2：分页导航中 keyword 参数丢失。**

A：检查 `th:href="@{/books(keyword=${keyword}, page=${page - 1})}"` 是否包含了 `keyword`。

---

### 任务 T6：单元测试

#### 任务信息（T6）

| 项目 | 内容 |
| --- | --- |
| 任务编码 | T6 |
| 任务名称 | 单元测试 |
| 建议用时 | 2 课时 |
| 前置任务 | T5 完成，搜索分页已生效 |

#### 学习目标（T6）

完成本任务后，学习者应能够：

- 使用 `Validator` 工厂直接测试 Bean Validation 规则。
- 使用 Mockito 模拟 Mapper 层，测试 Service 层分页逻辑。
- 理解单元测试与集成测试的区别。

#### 前置状态（T6）

系统当前没有单元测试（或仅有默认的上下文加载测试）。新增校验和分页逻辑需要测试保障。

#### 涉及文件（T6）

- [src/test/java/com/example/bookmanagement/BookValidationTest.java](src/test/java/com/example/bookmanagement/BookValidationTest.java)（新增）
- [src/test/java/com/example/bookmanagement/BookServiceTest.java](src/test/java/com/example/bookmanagement/BookServiceTest.java)（新增）

#### 核心概念（T6）

- `Validation.buildDefaultValidatorFactory().getValidator()`：获取校验器。
- `@ExtendWith(MockitoExtension.class)`：启用 Mockito。
- `@Mock` 和 `@InjectMocks`：模拟依赖并注入被测对象。

#### 理解 Checkpoint（T6）

1. 为什么要用 Mockito 模拟 `BookMapper`，而不是直接连接真实数据库？
2. 单元测试和 `@SpringBootTest` 有什么区别？
3. 测试失败时，应该先看什么信息来定位问题？

#### 操作步骤（T6）

##### 步骤 T6.1：校验测试

**操作**：创建 `BookValidationTest.java`。

**预期结果**：运行测试时，空白书名、书名过长等非法输入应产生约束违规，合法输入应通过校验。

**AI 提示词模板**：

```text
我想为 Book 实体和 RegisterRequest 的校验注解编写单元测试。
请告诉我如何使用 Validation.buildDefaultValidatorFactory().getValidator() 获取校验器，并给出测试空白书名的示例。
```

**关键代码参考**：

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

    @Test
    void bookNameTooLong_shouldFail() {
        Book book = new Book();
        book.setName("a".repeat(101));
        assertFalse(validator.validate(book).isEmpty());
    }

    @Test
    void validBook_shouldPass() {
        Book book = new Book();
        book.setName("Spring Boot 实战");
        assertTrue(validator.validate(book).isEmpty());
    }
}
```

##### 步骤 T6.2：Service 测试

**操作**：创建 `BookServiceTest.java`。

**预期结果**：Mock `BookMapper` 后，`findBooks` 返回的 `PageResult` 字段正确。

**AI 提示词模板**：

```text
我想用 Mockito 测试 BookService.findBooks 的分页计算逻辑。
请告诉我如何使用 @ExtendWith(MockitoExtension.class)、@Mock 和 @InjectMocks，并解释为什么不需要启动 Spring 容器。
```

**关键代码参考**：

```java
@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    @Test
    void findBooks_shouldReturnPagedResult() {
        when(bookMapper.findByKeyword(anyString(), anyInt(), anyInt())).thenReturn(List.of(new Book()));
        when(bookMapper.countByKeyword(anyString())).thenReturn(25L);

        PageResult<Book> result = bookService.findBooks("test", 2, 10);

        assertEquals(2, result.page());
        assertEquals(10, result.size());
        assertEquals(25, result.total());
        assertEquals(3, result.totalPages());
        assertEquals(1, result.content().size());
    }
}
```

#### 验证清单（T6）

- [ ] 运行 `mvn clean test`，所有测试通过。
- [ ] 故意改错一个测试断言，确认 Maven 会报告测试失败。
- [ ] 检查测试报告中是否有被忽略的测试或异常信息。

#### 提交检查点（T6）

- 新增 `BookValidationTest.java` 和 `BookServiceTest.java`。
- 保留 `mvn clean test` 运行成功的终端截图。

#### 常见问题（T6）

**Q1：运行测试时报错，提示无法连接数据库。**

A：本任务的测试应为纯单元测试，不依赖 Spring 容器和数据库。请检查是否误加了 `@SpringBootTest`。

**Q2：Mockito 提示 `UnnecessaryStubbingException`。**

A：检查测试中是否设置了未使用的 `when(...)`，或未正确调用被测方法。

---

### 任务 T7：版本记录与 Git 提交

#### 任务信息（T7）

| 项目 | 内容 |
| --- | --- |
| 任务编码 | T7 |
| 任务名称 | 版本记录与 Git 提交 |
| 建议用时 | 1 课时 |
| 前置任务 | T1~T6 全部完成并通过验证 |

#### 学习目标（T7）

完成本任务后，学习者应能够：

- 撰写规范的 CHANGELOG。
- 按功能拆分 Git 提交。
- 检查敏感信息，避免将个人密码提交到仓库。

#### 前置状态（T7）

已完成全部代码改造和测试，但未整理版本记录和提交历史。

#### 涉及文件（T7）

- [CHANGELOG.md](CHANGELOG.md)（新增或更新）
- [.gitignore](.gitignore)
- [src/main/resources/application.yml](src/main/resources/application.yml)

#### 核心概念（T7）

- CHANGELOG：记录项目版本变更的文档。
- Git 提交规范：`feat:`、`fix:`、`docs:`、`test:` 等前缀。
- `.gitignore`：告诉 Git 哪些文件不应被跟踪。

#### 理解 Checkpoint（T7）

1. 为什么要按功能拆分提交，而不是一次性提交所有改动？
2. `application.yml` 中的密码为什么不能提交到公共仓库？
3. `CHANGELOG.md` 与 Git 提交信息有什么区别？

#### 操作步骤（T7）

##### 步骤 T7.1：创建或更新 CHANGELOG

**操作**：创建 `CHANGELOG.md`，按模块记录本次所有改动。

**预期结果**：CHANGELOG 包含版本概述、功能变更、依赖变更、数据库变更、API 矩阵、验证方式等。

**AI 提示词模板**：

```text
请帮我为图书管理系统编写一份 CHANGELOG.md，要求包含：
1. 版本号和日期；
2. 本次实现的功能清单；
3. 涉及文件；
4. 依赖变更；
5. API 端点与权限矩阵；
6. 验证方式。
```

**关键代码参考**：

参见项目已存在的 [CHANGELOG.md](CHANGELOG.md)。

##### 步骤 T7.2：检查敏感信息

**操作**：打开 `application.yml`，确认 `password` 字段为教学默认 `123456`，而不是你的本地真实密码。

**预期结果**：本地运行时使用真实密码，但提交前恢复为 `123456`。

##### 步骤 T7.3：更新 .gitignore

**操作**：检查 `.gitignore` 是否已排除 `target/`、`.idea/`、验证截图等不需要提交的文件。

**预期结果**：`git status` 不会显示编译产物、IDE 配置或截图文件。

##### 步骤 T7.4：按功能拆分提交

**操作**：将改动按功能域分组提交。

**预期结果**：Git 历史清晰，每个提交对应一个完整功能。

**推荐提交顺序**：

```bash
git add src/main/java/com/example/bookmanagement/config/SecurityConfig.java \
        src/main/java/com/example/bookmanagement/controller/BookController.java \
        src/main/resources/templates/books/list.html \
        src/main/resources/templates/books/add.html \
        src/main/resources/templates/books/detail.html \
        src/main/resources/templates/books/edit.html
git commit -m "feat: 新增角色权限控制与完整图书 CRUD"

git add src/main/java/com/example/bookmanagement/entity/Book.java \
        src/main/java/com/example/bookmanagement/dto/RegisterRequest.java \
        src/main/java/com/example/bookmanagement/controller/AuthController.java \
        src/main/java/com/example/bookmanagement/controller/BookController.java \
        src/main/resources/templates/books/add.html \
        src/main/resources/templates/books/edit.html \
        src/main/resources/templates/register.html \
        src/main/java/com/example/bookmanagement/exception/GlobalExceptionHandler.java \
        src/main/resources/templates/error.html \
        src/main/java/com/example/bookmanagement/dto/PageResult.java \
        src/main/java/com/example/bookmanagement/mapper/BookMapper.java \
        src/main/java/com/example/bookmanagement/service/BookService.java \
        src/main/resources/templates/books/list.html
git commit -m "feat: 新增服务端校验、全局异常处理与搜索分页"

git add src/test/java/com/example/bookmanagement/BookValidationTest.java \
        src/test/java/com/example/bookmanagement/BookServiceTest.java \
        CHANGELOG.md \
        .gitignore
git commit -m "test & docs: 新增单元测试、CHANGELOG 与文档同步"
```

#### 验证清单（T7）

- [ ] `git log --oneline` 显示至少 3 个清晰的提交。
- [ ] `git status --short` 没有未提交的代码文件。
- [ ] `application.yml` 中的密码为 `123456`。
- [ ] `mvn clean test` 全部通过。
- [ ] 项目可正常启动，手工验证 T1~T5 的核心功能。

#### 提交检查点（T7）

- CHANGELOG.md 已创建或更新。
- Git 提交历史清晰。
- 保留 `git log --oneline` 的截图。

#### 常见问题（T7）

**Q1：提交时不小心把真实密码提交了，如何修改？**

A：如果只是最后一次提交，可以修改文件后执行 `git add` 和 `git commit --amend`。如果已推送到远程，需要更复杂的重写历史操作，建议联系教师处理。

**Q2：所有改动已经混在一起，不知道如何拆分提交。**

A：可以使用 `git add -p` 按文件块选择要提交的内容，或者先撤销暂存区，再按任务 T1~T7 分组 `git add`。

---

## 第 8 章 提交与验收规范

### 8.1 最终提交物

学习者需提交以下内容：

1. 完整的项目源码（Git 仓库）。
2. 更新后的 [CHANGELOG.md](CHANGELOG.md)。
3. 本任务书中各任务的验证截图或验证记录。
4. 个人学习笔记（含理解 checkpoint 答案、AI 对话关键截图）。

### 8.2 验收流程

1. 教师或助教克隆仓库。
2. 执行 `mvn clean test`，确认测试通过。
3. 启动项目，按第 7 章验证清单逐项检查。
4. 抽查学生解释某段关键代码的含义。
5. 检查 `git log`、`CHANGELOG.md` 和 `.gitignore`。

### 8.3 答辩准备

每位学习者需准备回答以下问题中的至少 3 个：

1. `@EnableMethodSecurity` 和 `@PreAuthorize` 是如何配合工作的？
2. 为什么编辑图书时要先查询再回显，而不是直接新建表单？
3. 前端 `required` 已经能阻止空提交，为什么后端还要校验？
4. `@ControllerAdvice` 能捕获哪些异常？它和普通 Controller 中的异常处理有什么区别？
5. MyBatis 动态 SQL 中的 `<where>` 标签有什么作用？
6. 单元测试中使用 Mockito 的好处是什么？
7. 你是如何使用 Git 拆分提交的？为什么这样拆分？

## 第 9 章 附录

### 附录 A：推荐 AI 提示词速查表

| 场景 | 提示词 |
| --- | --- |
| 解释代码 | 请解释【文件路径】中【方法名】的作用，以及它在整个请求处理流程中的位置。 |
| 实现功能 | 我想实现【目标】。当前相关文件有【文件列表】。请告诉我需要改哪些文件、每处改动的原因以及验证方法。 |
| 排查错误 | 我运行【命令】时遇到以下错误：【错误信息】。请帮我分析可能的原因和排查步骤。 |
| 代码审查 | 请帮我审查以下代码片段，指出潜在的安全、性能或可读性问题：【代码】。 |

### 附录 B：代码审查清单

在提交前，请逐项自查：

- [ ] 没有硬编码密码、API 密钥等敏感信息。
- [ ] 没有 SQL 拼接，所有 SQL 参数均使用 `#{}`。
- [ ] Controller 中处理的用户输入都经过校验。
- [ ] 前端按钮权限与后端接口权限一致。
- [ ] 新增方法有对应的测试或手工验证。
- [ ] 没有未使用的导入和变量。
- [ ] 命名符合 Java 和 HTML 的常规规范。
- [ ] 异常被合理捕获或抛出，不会直接暴露堆栈给用户。

### 附录 C：Git 常用命令速查

```bash
# 查看当前状态
git status

# 查看修改内容
git diff

# 添加文件到暂存区
git add 文件名

# 提交
git commit -m "feat: 新增角色权限控制"

# 查看提交历史
git log --oneline

# 撤销工作区修改
git checkout -- 文件名

# 修改最后一次提交
git commit --amend
```

### 附录 D：常见错误与解决方案

| 错误现象 | 可能原因 | 解决方案 |
| --- | --- | --- |
| 数据库连接失败 | `application.yml` 密码或端口错误 | 检查本地 MySQL 配置 |
| 页面中文乱码 | 数据库字符集非 utf8mb4 | 执行 `book-management.sql` 重新初始化 |
| `@PreAuthorize` 不生效 | 未加 `@EnableMethodSecurity` | 在 `SecurityConfig` 上添加该注解 |
| 校验不生效 | 缺少 `@Valid` 或 `BindingResult` | 检查 Controller 方法参数 |
| Thymeleaf 报错 | 模板语法错误或属性名不一致 | 检查 `th:*` 表达式和 Model 中的属性名 |
| 测试连接数据库 | 误用 `@SpringBootTest` | 使用纯单元测试，不启动 Spring 容器 |

### 附录 E：拓展任务详细说明

完成核心任务后，可选择以下方向进行拓展。每个拓展任务都需要：

1. 画出涉及的表结构和 URL 设计。
2. 列出新增或修改的文件。
3. 提供端到端的手工验证。
4. 更新 [CHANGELOG.md](CHANGELOG.md)。

#### E.1 借阅功能

新增 `borrow_records` 表，记录用户借书/还书行为。ADMIN 可查看所有记录，USER 可查看自己的记录。

#### E.2 图书分类管理

将 `category` 从字符串改为独立字典表，支持 ADMIN 对分类进行增删改查。

#### E.3 操作日志

使用 AOP 记录管理员对图书的新增、编辑、删除操作，包括操作人、时间、操作内容。

#### E.4 密码强度提示

在注册页前端实时提示密码复杂度，并在后端增加密码强度校验规则。
