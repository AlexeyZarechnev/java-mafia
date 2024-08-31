package io.github.alexeyzarechnev.mafia;

public abstract class Role {
    protected final Player player;

    protected Role(Player player) {
        this.player = player;
    }
}
