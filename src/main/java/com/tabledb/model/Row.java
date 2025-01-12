package com.tabledb.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Row {
    private final Map<String, Object> values;

    public Row() {
        this.values = new LinkedHashMap<>();
    }

    public void setValue(String column, Object value) {
        values.put(column, value);
    }

    public Object getValue(String column) {
        return values.get(column);
    }

    @Override
    public String toString() {
        return values.values().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }
}