package com.onlogn.onlogn.common.dto;

/**
 * 목록 응답을 위한 공통 meta 객체.
 */
public record ListMeta(PaginationMeta pagination) {

    public static ListMeta of(int offset, int limit, long total) {
        return new ListMeta(PaginationMeta.of(offset, limit, total));
    }
}
