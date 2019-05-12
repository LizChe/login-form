package com.codecool.login.view;

public class View {

    private final String RESET = "\u001B[0m";

    private void printText(String message) {
        System.out.println(message);
    }

    public void printError(String message) {
        final String RED = "\u001B[31m";
        printText(RED + message + RESET);
    }

    public void printSuccess(String message) {
        final String GREEN = "\u001B[32m";
        printText(GREEN + message + RESET);
    }
}