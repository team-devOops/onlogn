package com.onlogn.onlogn.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 공통 성공 envelope (data + optional meta).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DataMetaEnvelope<T>(T data, Object meta) {

    public static <T> DataMetaEnvelope<T> of(T data) {
        return new DataMetaEnvelope<>(data, null);
    }

    public static <T> DataMetaEnvelope<T> of(T data, Object meta) {
        return new DataMetaEnvelope<>(data, meta);
    }
}
