package com.tabledb.util;

import com.tabledb.condition.Condition;
import com.tabledb.exception.InvalidCommandException;
import java.util.*;
import java.util.regex.*;

public class QueryParser {
    private static final Pattern SELECT_PATTERN =
            Pattern.compile("SELECT\\s+(.+?)\\s+FROM\\s+(\\w+)(?:\\s+WHERE\\s+(.+))?",
                    Pattern.CASE_INSENSITIVE);

    public static class Query {
        private final List<String> columns;
        private final String tableName;
        private final Condition condition;

        public Query(List<String> columns, String tableName, Condition condition) {
            this.columns = columns;
            this.tableName = tableName;
            this.condition = condition;
        }

        public List<String> getColumns() { return columns; }
        public String getTableName() { return tableName; }
        public Condition getCondition() { return condition; }
    }

    public static Query parse(String queryString) {
        Matcher matcher = SELECT_PATTERN.matcher(queryString);
        if (!matcher.matches()) {
            throw new InvalidCommandException("Invalid query format");
        }

        String columnsStr = matcher.group(1);
        String tableName = matcher.group(2);
        String whereClause = matcher.group(3);

        List<String> columns = columnsStr.equals("*") ?
                List.of("*") :
                Arrays.asList(columnsStr.split("\\s*,\\s*"));

        Condition condition = whereClause != null ?
                com.tabledb.command.CommandParser.parseCondition(whereClause) :
                null;

        return new Query(columns, tableName, condition);
    }
}