package io.github.alexeyzarechnev.mafia.roles;

import io.github.alexeyzarechnev.mafia.Game;
import io.github.alexeyzarechnev.mafia.Player;
import io.github.alexeyzarechnev.mafia.Role;

public class Policeman extends Role {

    public static class Archive { private Archive() {} };
    private static final Archive archive = new Archive();

    public Policeman(Player player) {
        super(player);
    }

    @Override
    public boolean isBlack() { return false; }

    @Override
    public void action(Game game) { game.check(player.vote(), archive); }

}
