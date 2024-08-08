package com.alexeyzarechnev.mafia;

public interface Host {
    public Player getPlayerForAction(Action action);
    public void getMessage(String message);
}
