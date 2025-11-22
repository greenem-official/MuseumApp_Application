package org.daylight.museumapp.dto;

import lombok.Getter;

@Getter
public class ApiResult<T> {
    private final T data;
    private final String error;
    private final Throwable throwable;

    private ApiResult(T data, String error) {
        this.data = data;
        this.error = error;
        this.throwable = null;
    }

    private ApiResult(T data, String error, Throwable throwable) {
        this.data = data;
        this.error = error;
        this.throwable = throwable;
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(data, null);
    }

    public static <T> ApiResult<T> error(String error) {
        return new ApiResult<>(null, error);
    }

    public static <T> ApiResult<T> throwable(Throwable throwable) { return new ApiResult<>(null, throwable.getMessage(), throwable); }

    public boolean isSuccess() {
        return error == null;
    }

    public boolean isThrowable() {
        return throwable != null;
    }
}
