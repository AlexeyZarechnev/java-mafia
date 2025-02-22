package io.github.alexeyzarechnev.mafia;

import java.util.List;

public abstract class Role {
    protected final Player player;

    protected Role(Player player) {
        this.player = player;
    }

    public void sleep() { player.sleep(); }

    public void awake() { player.awake(); }

    public Player vote(List<Player> voteablePlayers) { return player.vote(voteablePlayers); }

    public Player getPlayer() { return player; }

    public void win(boolean isWinner) { player.message(isWinner ? "win" : "lose"); }

    // public abstract void action(Game game);

    public abstract boolean isBlack(); 
}
