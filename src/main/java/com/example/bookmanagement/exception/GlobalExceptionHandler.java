package com.example.bookmanagement.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
