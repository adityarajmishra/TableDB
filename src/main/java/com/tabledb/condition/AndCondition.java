package com.tabledb.condition;

import com.tabledb.model.Row;
import java.util.List;

public class AndCondition implements Condition {
    private final List<Condition> conditions;

    public AndCondition(List<Condition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public boolean evaluate(Row row) {
        return conditions.stream().allMatch(condition -> condition.evaluate(row));
    }
}