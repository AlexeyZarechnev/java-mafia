package io.github.alexeyzarechnev.mafia.exceptions;

public class IncorrectGameTimeException extends Exception {
    
    public IncorrectGameTimeException(boolean isDay) {
        super("expected " + (isDay ? "day" : "night"));
    }
}
