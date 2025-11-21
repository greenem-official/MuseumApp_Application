package org.daylight.museumapp.dto;

import lombok.Getter;

@Getter
public class ApiResult<T> {
    private final T data;
    private final String error;

    private ApiResult(T data, String error) {
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(data, null);
    }

    public static <T> ApiResult<T> error(String error) {
        return new ApiResult<>(null, error);
    }

    public boolean isSuccess() {
        return error == null;
    }

}
