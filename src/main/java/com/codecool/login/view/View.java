package com.codecool.login.view;

public class View {

    private void printText(String message) {
        System.out.println(message);
    }

    public void printError(String message) {
        System.err.println(message);
    }

    public void printSuccess(String message) {
        final String green = "\u001B[32m";
        final String reset = "\u001B[0m";
        printText(green + message + reset);
    }
}