package io.github.alexeyzarechnev.mafia.roles;

import io.github.alexeyzarechnev.mafia.Player;
import io.github.alexeyzarechnev.mafia.Role;

public class Citizen extends Role {

    public Citizen(Player player) throws IllegalArgumentException {
        super(player);
    }

    @Override
    public boolean isBlack() { return false; }

    

}
