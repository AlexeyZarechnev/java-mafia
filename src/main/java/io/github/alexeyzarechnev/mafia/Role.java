package io.github.alexeyzarechnev.mafia;

public abstract class Role {
    protected final Player player;

    protected Role(Player player) {
        this.player = player;
    }

    public void sleep() { player.sleep(); }

    public void awake() { player.awake(); }

    public Player vote() { return player.vote(); }

    public Player getPlayer() { return player; }

    public abstract boolean isBlack(); 
}
