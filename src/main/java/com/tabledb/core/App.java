package com.tabledb.core;

import com.tabledb.command.CommandProcessor;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        CommandProcessor processor = new CommandProcessor();
        Scanner scanner = new Scanner(System.in);

        while (processor.isRunning()) {
            String command = scanner.nextLine();
            String result = processor.processCommand(command);
            System.out.println(result);
        }
    }
}