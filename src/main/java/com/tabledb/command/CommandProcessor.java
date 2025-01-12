package com.tabledb.command;

import com.tabledb.condition.SimpleCondition;
import com.tabledb.core.Database;
import com.tabledb.model.Column;
import com.tabledb.model.DataType;
import com.tabledb.model.Row;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommandProcessor {
    private final Database database;
    private boolean running;

    public CommandProcessor() {
        this.database = new Database();
        this.running = true;
    }

    public String processCommand(String command) {
        try {
            if (command.equals("EXIT") || command.equals("STOP")) {
                running = false;
                return "Goodbye!";
            }

            if (command.startsWith("CREATE_TABLE")) {
                return handleCreateTable(command);
            } else if (command.startsWith("INSERT INTO")) {
                return handleInsert(command);
            } else if (command.startsWith("SELECT")) {
                return handleSelect(command);
            } else if (command.startsWith("DELETE FROM")) {
                return handleDelete(command);
            } else if (command.startsWith("UPDATE")) {
                return handleUpdate(command);
            } else if (command.equals("SHOW TABLES")) {
                return handleShowTables();
            } else if (command.equals("PURGE_AND_STOP")) {
                database.clear();
                running = false;
                return "PURGED, Goodbye!";
            }
            return "INVALID_COMMAND";
        } catch (Exception e) {
            return "INVALID_COMMAND";
        }
    }

    private String handleCreateTable(String command) {
        Pattern pattern = Pattern.compile("CREATE_TABLE\\s+(\\w+)\\s*\\((.+)\\)");
        Matcher matcher = pattern.matcher(command);

        if (!matcher.matches()) {
            return "INVALID_COMMAND";
        }

        try {
            String tableName = matcher.group(1);
            String[] columnDefs = matcher.group(2).split(",\\s*");

            List<Column> columns = Arrays.stream(columnDefs)
                    .map(String::trim)
                    .map(def -> {
                        String[] parts = def.split("\\s+");
                        if (parts.length != 2) throw new IllegalArgumentException();
                        return new Column(parts[0], DataType.valueOf(parts[1].toUpperCase()));
                    })
                    .collect(Collectors.toList());

            return database.createTable(tableName, columns) ? "SUCCESS" : "TABLE_EXISTS";
        } catch (Exception e) {
            return "INVALID_COMMAND";
        }
    }

    private String handleInsert(String command) {
        Pattern pattern = Pattern.compile("INSERT INTO (\\w+) VALUES \\((.+)\\)");
        Matcher matcher = pattern.matcher(command);

        if (!matcher.matches()) {
            return "INVALID_COMMAND";
        }

        try {
            String tableName = matcher.group(1);
            String valuesStr = matcher.group(2);

            // First check if table exists
            if (!database.getTableNames().contains(tableName)) {
                return "TABLE_NOT_FOUND";
            }

            // Validate values before parsing
            if (!isValidValueFormat(valuesStr)) {
                return "INVALID_COMMAND";
            }

            List<Object> values = parseValues(valuesStr);
            return database.insertIntoTable(tableName, values) ? "SUCCESS" : "INVALID_COMMAND";
        } catch (IllegalArgumentException e) {
            return "INVALID_COMMAND";
        }
    }

    private boolean isValidValueFormat(String valuesStr) {
        // Check for balanced quotes and proper value format
        boolean inQuotes = false;
        int commaCount = 0;

        for (char c : valuesStr.toCharArray()) {
            if (c == '"') inQuotes = !inQuotes;
            if (c == ',' && !inQuotes) commaCount++;
        }

        return !inQuotes; // Make sure all quotes are properly closed
    }

    private List<Object> parseValues(String valuesStr) {
        List<Object> values = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();
        boolean inQuotes = false;

        for (char c : valuesStr.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
                currentValue.append(c);
            } else if (c == ',' && !inQuotes) {
                values.add(parseValue(currentValue.toString().trim()));
                currentValue = new StringBuilder();
            } else {
                currentValue.append(c);
            }
        }

        if (currentValue.length() > 0) {
            values.add(parseValue(currentValue.toString().trim()));
        }

        return values;
    }

    private Object parseValue(String value) {
        value = value.trim();
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value format: " + value);
        }
    }

    private String handleSelect(String command) {
        Pattern pattern = Pattern.compile("SELECT (.+) FROM (\\w+)(?: WHERE (.+))?");
        Matcher matcher = pattern.matcher(command);

        if (!matcher.matches()) {
            return "INVALID_COMMAND";
        }

        try {
            String columns = matcher.group(1);
            String tableName = matcher.group(2);
            String whereClause = matcher.group(3);

            // First check if table exists
            if (!database.getTableNames().contains(tableName)) {
                return "TABLE_NOT_FOUND";
            }

            List<String> columnList = columns.equals("*") ?
                    Collections.emptyList() :
                    Arrays.asList(columns.split(",\\s*"));

            SimpleCondition condition = null;
            if (whereClause != null) {
                String[] parts = whereClause.split("\\s*=\\s*");
                if (parts.length != 2) return "INVALID_COMMAND";
                condition = new SimpleCondition(parts[0], parseValue(parts[1]));
            }

            List<Row> results = database.select(tableName, columnList, condition);
            if (results.isEmpty()) {
                return "NO_ROWS_FOUND";
            }

            return results.stream()
                    .map(Row::toString)
                    .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            return "INVALID_COMMAND";
        }
    }

    private String handleDelete(String command) {
        Pattern pattern = Pattern.compile("DELETE FROM (\\w+) WHERE (.+)");
        Matcher matcher = pattern.matcher(command);

        if (!matcher.matches()) {
            return "INVALID_COMMAND";
        }

        try {
            String tableName = matcher.group(1);

            // First check if table exists
            if (!database.getTableNames().contains(tableName)) {
                return "TABLE_NOT_FOUND";
            }

            String whereClause = matcher.group(2);
            String[] parts = whereClause.split("\\s*=\\s*");
            if (parts.length != 2) return "INVALID_COMMAND";

            SimpleCondition condition = new SimpleCondition(parts[0], parseValue(parts[1]));
            int deleted = database.delete(tableName, condition);

            return deleted > 0 ? "DELETED " + deleted : "NO_ROWS_DELETED";
        } catch (Exception e) {
            return "INVALID_COMMAND";
        }
    }

    private String handleUpdate(String command) {
        Pattern pattern = Pattern.compile("UPDATE (\\w+) SET (.+) WHERE (.+)");
        Matcher matcher = pattern.matcher(command);

        if (!matcher.matches()) {
            return "INVALID_COMMAND";
        }

        try {
            String tableName = matcher.group(1);

            // First check if table exists
            if (!database.getTableNames().contains(tableName)) {
                return "TABLE_NOT_FOUND";
            }

            String setClause = matcher.group(2);
            String whereClause = matcher.group(3);

            Map<String, Object> updates = Arrays.stream(setClause.split(",\\s*"))
                    .map(set -> set.split("\\s*=\\s*"))
                    .collect(Collectors.toMap(
                            parts -> parts[0],
                            parts -> parseValue(parts[1])
                    ));

            String[] whereParts = whereClause.split("\\s*=\\s*");
            if (whereParts.length != 2) return "INVALID_COMMAND";

            SimpleCondition condition = new SimpleCondition(whereParts[0], parseValue(whereParts[1]));
            int updated = database.update(tableName, updates, condition);

            return updated > 0 ? "UPDATED " + updated : "NO_ROWS_UPDATED";
        } catch (Exception e) {
            return "INVALID_COMMAND";
        }
    }

    private String handleShowTables() {
        Set<String> tableNames = database.getTableNames();
        return tableNames.isEmpty() ? "NO_TABLES_AVAILABLE" :
                String.join("\n", tableNames);
    }

    public boolean isRunning() {
        return running;
    }
}