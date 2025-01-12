package com.tabledb.condition;

import com.tabledb.model.Row;
import java.util.List;

public class OrCondition implements Condition {
    private final List<Condition> conditions;

    public OrCondition(List<Condition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public boolean evaluate(Row row) {
        return conditions.stream().anyMatch(condition -> condition.evaluate(row));
    }
}