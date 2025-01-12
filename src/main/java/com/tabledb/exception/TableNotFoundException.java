package com.tabledb.exception;

public class TableNotFoundException extends DatabaseException {
    public TableNotFoundException(String tableName) {
        super("Table not found: " + tableName);
    }
}