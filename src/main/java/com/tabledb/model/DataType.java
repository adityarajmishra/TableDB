package com.tabledb.model;

public enum DataType {
    INT, STRING;

    public boolean isValidValue(Object value) {
        return switch (this) {
            case INT -> value instanceof Integer || (value instanceof String && ((String) value).matches("-?\\d+"));
            case STRING -> value instanceof String;
        };
    }

    public Object parseValue(String value) {
        return switch (this) {
            case INT -> Integer.parseInt(value.replaceAll("\"", ""));
            case STRING -> value.replaceAll("^\"|\"$", "");
        };
    }
}