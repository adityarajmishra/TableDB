package com.tabledb.exception;

public class InvalidCommandException extends DatabaseException {
    public InvalidCommandException(String message) {
        super(message);
    }
}