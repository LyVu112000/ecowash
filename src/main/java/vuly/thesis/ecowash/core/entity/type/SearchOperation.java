package vuly.thesis.ecowash.core.entity.type;

public enum SearchOperation {
    EQUAL,
    JOIN_LIKE,
    JOIN_LIST_LIKE,
    JOIN_EQUAL,
    MULTI_JOIN_EQUAL,
    JOIN_LIST_EQUAL,
    JOIN,
    NOT_EQUAL,
    EQUAL_IGNORE_CASE,
    NOT_EQUAL_IGNORE_CASE,
    IN,
    NOT_IN,
    BETWEEN,
    NOT_BETWEEN,
    GREATER_THAN,
    GREATER_THAN_EQUAL,
    LESS_THAN,
    LESS_THAN_EQUAL,
    LIKE,
    LIKE_IGNORE_CASE,
    NOT_LIKE,
    NOT_LIKE_IGNORE_CASE,
    IS_NULL,
    IS_NOT_NULL,
    IS_EMPTY,
    IS_TRUE,
    IS_FALSE,
    EXISTED;
}
