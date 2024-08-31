package io.github.alexeyzarechnev.mafia.roles;

import io.github.alexeyzarechnev.mafia.Game;
import io.github.alexeyzarechnev.mafia.Player;
import io.github.alexeyzarechnev.mafia.Role;

public class Citizen extends Role {

    public Citizen(Player player) throws IllegalArgumentException {
        super(player);
        //TODO Auto-generated constructor stub
    }

    @Override
    public boolean isBlack() { return false; }

    @Override
    public void action(Game game) { throw new UnsupportedOperationException("Citizen can't do anything"); }

    

}
