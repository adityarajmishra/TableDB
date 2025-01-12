package com.tabledb.condition;

import com.tabledb.model.Row;

public class SimpleCondition implements Condition {
    private final String column;
    private final Object value;

    public SimpleCondition(String column, Object value) {
        this.column = column;
        this.value = value;
    }

    @Override
    public boolean evaluate(Row row) {
        Object rowValue = row.getValue(column);
        return value.equals(rowValue);
    }
}