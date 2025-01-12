package com.tabledb.condition;

import com.tabledb.model.Row;

@FunctionalInterface
public interface Condition {
    boolean evaluate(Row row);
}