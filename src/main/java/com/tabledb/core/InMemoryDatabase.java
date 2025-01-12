package com.tabledb.core;

import com.tabledb.model.Table;
import com.tabledb.condition.Condition;
import com.tabledb.model.Column;
import com.tabledb.model.Row;
import com.tabledb.exception.TableNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDatabase extends Database {
    private final Map<String, Table> tables;

    public InMemoryDatabase() {
        this.tables = new ConcurrentHashMap<>();
    }

    @Override
    public boolean createTable(String name, List<Column> columns) {
        if (tables.containsKey(name)) return false;
        tables.put(name, new Table(name, columns));
        return true;
    }

    @Override
    public boolean insertIntoTable(String tableName, List<Object> values) {
        Table table = tables.get(tableName);
        if (table == null) throw new TableNotFoundException(tableName);
        return table.insertRow(values);
    }

    @Override
    public List<Row> select(String tableName, List<String> columns, Condition condition) {
        Table table = tables.get(tableName);
        if (table == null) throw new TableNotFoundException(tableName);
        return table.select(columns, condition);
    }

    @Override
    public int delete(String tableName, Condition condition) {
        Table table = tables.get(tableName);
        if (table == null) throw new TableNotFoundException(tableName);
        return table.delete(condition);
    }

    @Override
    public int update(String tableName, Map<String, Object> updates, Condition condition) {
        Table table = tables.get(tableName);
        if (table == null) throw new TableNotFoundException(tableName);
        return table.update(updates, condition);
    }

    @Override
    public Set<String> getTableNames() {
        return tables.keySet();
    }

    @Override
    public void clear() {
        tables.clear();
    }
}