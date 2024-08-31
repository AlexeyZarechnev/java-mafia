package io.github.alexeyzarechnev.mafia.roles;

import io.github.alexeyzarechnev.mafia.Player;
import io.github.alexeyzarechnev.mafia.Role;

public class Mafia extends Role {

    public Mafia(Player player) {
        super(player);
    }

    @Override
    public boolean isBlack() { return true; }

}
