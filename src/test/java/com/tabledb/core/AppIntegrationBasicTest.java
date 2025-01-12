package com.tabledb.core;

import org.junit.jupiter.api.*;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


import static org.junit.jupiter.api.Assertions.assertEquals;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppIntegrationBasicTest {


    private void assertAppOutput(String input, String expectedOutput) {
        // Set up input stream
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);


        // Set up output stream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));


        // Run the main method
        App.main(new String[]{});


        // Capture and process the output
        String actualOutput = out.toString().replaceAll("\\r\\n", "\n").trim();
        String formattedExpectedOutput = expectedOutput.replaceAll("\\r\\n", "\n").trim();


        assertEquals(formattedExpectedOutput, actualOutput);
    }


    @Test
    @Order(1)
    public void testCreateTable() {
        String input = "CREATE_TABLE users (id INT, name STRING)\nCREATE_TABLE users (id INT, name STRING)\nCREATE_TABLE invalid\nEXIT";
        String expectedOutput = "SUCCESS\nTABLE_EXISTS\nINVALID_COMMAND\nGoodbye!";


        assertAppOutput(input, expectedOutput);
    }


    @Test
    @Order(2)
    public void testInsertIntoTable() {
        String input = "CREATE_TABLE users (id INT, name STRING)\nINSERT INTO users VALUES (1, \"John\")\nINSERT INTO users VALUES (\"invalid\", \"John\")\nINSERT INTO non_existing VALUES (1, \"John\")\nEXIT";
        String expectedOutput = "SUCCESS\nSUCCESS\nINVALID_COMMAND\nTABLE_NOT_FOUND\nGoodbye!";


        assertAppOutput(input, expectedOutput);
    }


    @Test
    @Order(3)
    public void testDeleteRows() {
        String input = "CREATE_TABLE users (id INT, name STRING)\nINSERT INTO users VALUES (1, \"John\")\nDELETE FROM users WHERE id = 1\nDELETE FROM users WHERE id = 2\nDELETE FROM non_existing WHERE id = 1\nEXIT";
        String expectedOutput = "SUCCESS\nSUCCESS\nDELETED 1\nNO_ROWS_DELETED\nTABLE_NOT_FOUND\nGoodbye!";


        assertAppOutput(input, expectedOutput);
    }


    @Test
    @Order(4)
    public void testSelectRows() {
        String input = "CREATE_TABLE users (id INT, name STRING)\nINSERT INTO users VALUES (1, \"John\")\nSELECT * FROM users WHERE id = 1\nSELECT id FROM users WHERE id = 2\nSELECT * FROM non_existing WHERE id = 1\nEXIT";
        String expectedOutput = "SUCCESS\nSUCCESS\n1, John\nNO_ROWS_FOUND\nTABLE_NOT_FOUND\nGoodbye!";


        assertAppOutput(input, expectedOutput);
    }


    @Test
    @Order(5)
    public void testUpdateRows() {
        String input = "CREATE_TABLE users (id INT, name STRING)\nINSERT INTO users VALUES (1, \"John\")\nUPDATE users SET name = \"Doe\" WHERE id = 1\nUPDATE users SET name = \"Smith\" WHERE id = 2\nUPDATE non_existing SET name = \"Doe\" WHERE id = 1\nEXIT";
        String expectedOutput = "SUCCESS\nSUCCESS\nUPDATED 1\nNO_ROWS_UPDATED\nTABLE_NOT_FOUND\nGoodbye!";


        assertAppOutput(input, expectedOutput);
    }


    @Test
    @Order(6)
    public void testShowTables() {
        String input = "SHOW TABLES\nCREATE_TABLE users (id INT, name STRING)\nSHOW TABLES\nEXIT";
        String expectedOutput = "NO_TABLES_AVAILABLE\nSUCCESS\nusers\nGoodbye!";


        assertAppOutput(input, expectedOutput);
    }
}
