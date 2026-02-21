package com.onlogn.onlogn.common.dto;

/**
 * 목록 응답용 공통 페이지네이션 메타데이터.
 */
public record PaginationMeta(int offset, int limit, long total, String sort) {

    public static final String DEFAULT_SORT = "created_at desc, id desc";

    public static PaginationMeta of(int offset, int limit, long total) {
        return new PaginationMeta(offset, limit, total, DEFAULT_SORT);
    }
}
