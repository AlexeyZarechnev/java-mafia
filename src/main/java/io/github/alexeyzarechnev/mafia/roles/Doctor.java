package io.github.alexeyzarechnev.mafia.roles;

import io.github.alexeyzarechnev.mafia.Player;
import io.github.alexeyzarechnev.mafia.Role;

public class Doctor extends Role {

    public Doctor(Player player) {
        super(player);
    }

    @Override
    public boolean isBlack() { return false; }

    
}
