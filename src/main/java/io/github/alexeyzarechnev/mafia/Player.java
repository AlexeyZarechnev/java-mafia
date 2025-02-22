package io.github.alexeyzarechnev.mafia;

import java.util.List;

public interface Player {
    public void sleep();
    public void awake();
    public Player vote(List<Player> voteablePlayers);
    public void message(String message);
}
