package io.github.alexeyzarechnev.mafia.roles;

import io.github.alexeyzarechnev.mafia.Game;
import io.github.alexeyzarechnev.mafia.Player;
import io.github.alexeyzarechnev.mafia.Role;

public class Mafia extends Role {

    public static class Weapon { private Weapon() {} };
    private static final Weapon weapon = new Weapon();

    public Mafia(Player player) {
        super(player);
    }

    @Override
    public boolean isBlack() { return true; }

    @Override
    public void action(Game game) { game.injure(player.vote(), weapon); }

}
