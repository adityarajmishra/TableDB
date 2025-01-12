package com.tabledb.model;

import java.util.List;

public class Result {
    private final String message;
    private final List<Row> rows;
    private final boolean success;

    private Result(String message, List<Row> rows, boolean success) {
        this.message = message;
        this.rows = rows;
        this.success = success;
    }

    public static Result success(String message) {
        return new Result(message, List.of(), true);
    }

    public static Result success(List<Row> rows) {
        return new Result(null, rows, true);
    }

    public static Result error(String message) {
        return new Result(message, List.of(), false);
    }

    public String getMessage() {
        return message;
    }

    public List<Row> getRows() {
        return rows;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        if (!success) return message;
        if (rows.isEmpty()) return message;
        return rows.stream()
                .map(Row::toString)
                .collect(java.util.stream.Collectors.joining("\n"));
    }
}