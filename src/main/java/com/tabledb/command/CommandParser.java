package com.tabledb.command;

import com.tabledb.exception.InvalidCommandException;
import com.tabledb.model.Column;
import com.tabledb.model.DataType;
import com.tabledb.condition.Condition;
import com.tabledb.condition.SimpleCondition;
import com.tabledb.condition.AndCondition;
import com.tabledb.condition.OrCondition;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommandParser {
    private static final Pattern CREATE_PATTERN = Pattern.compile("CREATE_TABLE\\s+(\\w+)\\s*\\((.+)\\)");
    private static final Pattern INSERT_PATTERN = Pattern.compile("INSERT\\s+INTO\\s+(\\w+)\\s+VALUES\\s*\\((.+)\\)");
    private static final Pattern SELECT_PATTERN = Pattern.compile("SELECT\\s+(.+?)\\s+FROM\\s+(\\w+)(?:\\s+WHERE\\s+(.+))?");
    private static final Pattern UPDATE_PATTERN = Pattern.compile("UPDATE\\s+(\\w+)\\s+SET\\s+(.+?)\\s+WHERE\\s+(.+)");
    private static final Pattern DELETE_PATTERN = Pattern.compile("DELETE\\s+FROM\\s+(\\w+)\\s+WHERE\\s+(.+)");

    public static List<Column> parseColumns(String columnsStr) {
        return Arrays.stream(columnsStr.split(","))
                .map(String::trim)
                .map(colStr -> {
                    String[] parts = colStr.split("\\s+");
                    if (parts.length != 2) throw new InvalidCommandException("Invalid column definition");
                    return new Column(parts[0], DataType.valueOf(parts[1].toUpperCase()));
                })
                .collect(Collectors.toList());
    }

    public static List<Object> parseValues(String valuesStr) {
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

    private static Object parseValue(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Invalid value format: " + value);
        }
    }

    public static Condition parseCondition(String conditionStr) {
        if (conditionStr.contains(" AND ")) {
            String[] conditions = conditionStr.split(" AND ");
            return new AndCondition(
                    Arrays.stream(conditions)
                            .map(CommandParser::parseSimpleCondition)
                            .collect(Collectors.toList())
            );
        } else if (conditionStr.contains(" OR ")) {
            String[] conditions = conditionStr.split(" OR ");
            return new OrCondition(
                    Arrays.stream(conditions)
                            .map(CommandParser::parseSimpleCondition)
                            .collect(Collectors.toList())
            );
        } else {
            return parseSimpleCondition(conditionStr);
        }
    }

    private static SimpleCondition parseSimpleCondition(String condition) {
        String[] parts = condition.trim().split("\\s*=\\s*");
        if (parts.length != 2) throw new InvalidCommandException("Invalid condition format");
        return new SimpleCondition(parts[0], parseValue(parts[1]));
    }

    public static Map<String, Object> parseSetClause(String setClause) {
        return Arrays.stream(setClause.split(","))
                .map(String::trim)
                .map(assignment -> assignment.split("\\s*=\\s*"))
                .collect(Collectors.toMap(
                        parts -> parts[0],
                        parts -> parseValue(parts[1])
                ));
    }
}