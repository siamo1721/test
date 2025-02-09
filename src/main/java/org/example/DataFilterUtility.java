package org.example;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DataFilterUtility {

    private static final String DEFAULT_OUTPUT_DIR = ".";
    private static final String DEFAULT_PREFIX = "";
    private static final String INTEGERS_FILE = "integers.txt";
    private static final String FLOATS_FILE = "floats.txt";
    private static final String STRINGS_FILE = "strings.txt";

    private static String outputDir = DEFAULT_OUTPUT_DIR;
    private static String prefix = DEFAULT_PREFIX;
    private static boolean appendMode = false;
    private static boolean shortStats = false;
    private static boolean fullStats = false;

    private static final List<Integer> integers = new ArrayList<>();
    private static final List<Double> floats = new ArrayList<>();
    private static final List<String> strings = new ArrayList<>();

    public static void main(String[] args) {
        List<String> inputFiles = new ArrayList<>();
        parseArguments(args, inputFiles);

        if (inputFiles.isEmpty()) {
            System.err.println("Ошибка: Не указаны входные файлы.");
            return;
        }

        try {
            processFiles(inputFiles);
            writeResults();
            printStatistics();
        } catch (IOException e) {
            System.err.println("Ошибка при обработке файлов: " + e.getMessage());
        }
    }

    private static void parseArguments(String[] args, List<String> inputFiles) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-o":
                    outputDir = args[++i];
                    break;
                case "-p":
                    prefix = args[++i];
                    break;
                case "-a":
                    appendMode = true;
                    break;
                case "-s":
                    shortStats = true;
                    break;
                case "-f":
                    fullStats = true;
                    break;
                default:
                    if (args[i].endsWith(".txt")) {
                        inputFiles.add(args[i]);
                    }
                    break;
            }
        }
    }

    private static void processFiles(List<String> inputFiles) throws IOException {
        for (String file : inputFiles) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    processLine(line);
                }
            } catch (IOException e) {
                System.err.println("Ошибка при чтении файла " + file + ": " + e.getMessage());
            }
        }
    }

    private static void processLine(String line) {
        try {
            int intValue = Integer.parseInt(line);
            integers.add(intValue);
        } catch (NumberFormatException e1) {
            try {
                double floatValue = Double.parseDouble(line);
                floats.add(floatValue);
            } catch (NumberFormatException e2) {
                strings.add(line);
            }
        }
    }

    private static void writeResults() throws IOException {
        writeToFile(integers, prefix + INTEGERS_FILE);
        writeToFile(floats, prefix + FLOATS_FILE);
        writeToFile(strings, prefix + STRINGS_FILE);
    }

    private static <T> void writeToFile(List<T> data, String fileName) throws IOException {
        if (data.isEmpty()) {
            return;
        }

        Path filePath = Paths.get(outputDir, fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, appendMode ? StandardOpenOption.APPEND : StandardOpenOption.CREATE)) {
            for (T item : data) {
                writer.write(item.toString());
                writer.newLine();
            }
        }
    }

    private static void printStatistics() {
        if (shortStats || fullStats) {
            System.out.println("Статистика:");
            printIntegerStats();
            printFloatStats();
            printStringStats();
        }
    }

    private static void printIntegerStats() {
        if (!integers.isEmpty()) {
            System.out.println("Целые числа:");
            System.out.println("  Количество: " + integers.size());
            if (fullStats) {
                System.out.println("  Минимальное: " + Collections.min(integers));
                System.out.println("  Максимальное: " + Collections.max(integers));
                System.out.println("  Сумма: " + integers.stream().mapToInt(Integer::intValue).sum());
                System.out.println("  Среднее: " + integers.stream().mapToInt(Integer::intValue).average().orElse(0));
            }
        }
    }

    private static void printFloatStats() {
        if (!floats.isEmpty()) {
            System.out.println("Вещественные числа:");
            System.out.println("  Количество: " + floats.size());
            if (fullStats) {
                System.out.println("  Минимальное: " + Collections.min(floats));
                System.out.println("  Максимальное: " + Collections.max(floats));
                System.out.println("  Сумма: " + floats.stream().mapToDouble(Double::doubleValue).sum());
                System.out.println("  Среднее: " + floats.stream().mapToDouble(Double::doubleValue).average().orElse(0));
            }
        }
    }

    private static void printStringStats() {
        if (!strings.isEmpty()) {
            System.out.println("Строки:");
            System.out.println("  Количество: " + strings.size());
            if (fullStats) {
                System.out.println("  Самая короткая строка: " + strings.stream().mapToInt(String::length).min().orElse(0));
                System.out.println("  Самая длинная строка: " + strings.stream().mapToInt(String::length).max().orElse(0));
            }
        }
    }
}