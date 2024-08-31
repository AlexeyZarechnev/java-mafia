package io.github.alexeyzarechnev.mafia.roles;

import io.github.alexeyzarechnev.mafia.Game;
import io.github.alexeyzarechnev.mafia.Player;
import io.github.alexeyzarechnev.mafia.Role;

public class Doctor extends Role {

    public static class Medicine { private Medicine() {} };
    private static final Medicine medicine = new Medicine();

    public Doctor(Player player) {
        super(player);
    }

    @Override
    public boolean isBlack() { return false; }

    @Override
    public void action(Game game) { game.heal(player.vote(), medicine); }

}
