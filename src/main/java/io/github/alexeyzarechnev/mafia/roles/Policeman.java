package io.github.alexeyzarechnev.mafia.roles;

import io.github.alexeyzarechnev.mafia.Player;
import io.github.alexeyzarechnev.mafia.Role;

public class Policeman extends Role {

    public Policeman(Player player) {
        super(player);
    }

    @Override
    public boolean isBlack() { return false; }

}
