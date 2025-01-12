package com.tabledb.model;

public record Column(String name, DataType type) {
    public Column {
        if (name == null || name.trim().isEmpty() || !name.matches("[a-zA-Z0-9]+")) {
            throw new IllegalArgumentException("Invalid column name");
        }
    }
}