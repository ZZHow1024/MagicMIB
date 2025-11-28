package com.zzhow.magicmibbackend.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 封装统一的结果
 *
 * @author ZZHow
 * create 2025/11/28
 * update 2025/11/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code; // 代码
    private String message; // 信息
    private T data; // 数据

    public static <T> Result<T> success() {
        return new Result<>(0, "success", null);
    }

    public static <T> Result<T> success(String message, T object) {
        return new Result<>(0, message, object);
    }

    public static <T> Result<T> success(T object) {
        return new Result<>(0, "success", object);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(1, message, null);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(401, message, null);
    }

    public static <T> Result<T> resourceNotFound(String message) {
        return new Result<>(404, message, null);
    }
}
