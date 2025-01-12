// model/Table.java
package com.tabledb.model;

import com.tabledb.condition.Condition;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class Table {
    private final String name;
    private final Map<String, Column> columns;
    private final List<Row> rows;
    private final ReadWriteLock lock;

    public Table(String name, List<Column> columnList) {
        this.name = name;
        this.columns = new LinkedHashMap<>();
        columnList.forEach(col -> columns.put(col.name(), col));
        this.rows = new ArrayList<>();
        this.lock = new ReentrantReadWriteLock();
    }

    public boolean insertRow(List<Object> values) {
        if (values.size() != columns.size()) return false;

        try {
            lock.writeLock().lock();
            Row row = new Row();
            int i = 0;
            for (Column column : columns.values()) {
                Object value = values.get(i++);
                if (!column.type().isValidValue(value)) return false;
                row.setValue(column.name(), value);
            }
            rows.add(row);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Row> select(List<String> columnNames, Condition condition) {
        try {
            lock.readLock().lock();
            return rows.stream()
                    .filter(row -> condition == null || condition.evaluate(row))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    public int delete(Condition condition) {
        try {
            lock.writeLock().lock();
            int initialSize = rows.size();
            rows.removeIf(row -> condition.evaluate(row));
            return initialSize - rows.size();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int update(Map<String, Object> updates, Condition condition) {
        try {
            lock.writeLock().lock();
            return (int) rows.stream()
                    .filter(row -> condition.evaluate(row))
                    .peek(row -> updates.forEach(row::setValue))
                    .count();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String getName() {
        return name;
    }

    public Map<String, Column> getColumns() {
        return new LinkedHashMap<>(columns);
    }
}