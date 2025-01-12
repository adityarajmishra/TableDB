package com.tabledb.core;

import com.tabledb.condition.Condition;
import com.tabledb.model.Column;
import com.tabledb.model.Row;
import com.tabledb.model.Table;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Database {
    private final Map<String, Table> tables;

    public Database() {
        this.tables = new ConcurrentHashMap<>();
    }

    public boolean createTable(String name, List<Column> columns) {
        if (tables.containsKey(name)) return false;
        Table table = new Table(name, columns);
        tables.put(name, table);
        return true;
    }

    public boolean insertIntoTable(String tableName, List<Object> values) {
        Table table = tables.get(tableName);
        return table != null && table.insertRow(values);
    }

    public List<Row> select(String tableName, List<String> columns, Condition condition) {
        Table table = tables.get(tableName);
        return table != null ? table.select(columns, condition) : Collections.emptyList();
    }

    public int delete(String tableName, Condition condition) {
        Table table = tables.get(tableName);
        return table != null ? table.delete(condition) : -1;
    }

    public int update(String tableName, Map<String, Object> updates, Condition condition) {
        Table table = tables.get(tableName);
        return table != null ? table.update(updates, condition) : -1;
    }

    public Set<String> getTableNames() {
        return new HashSet<>(tables.keySet());
    }

    public void clear() {
        tables.clear();
    }
}