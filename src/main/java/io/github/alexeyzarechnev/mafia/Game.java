package io.github.alexeyzarechnev.mafia;

import java.util.Collections;
import java.util.List;

import io.github.alexeyzarechnev.mafia.roles.*;
import io.github.alexeyzarechnev.mafia.roles.exceptions.IncorrectGameTimeException;

public class Game {

    @FunctionalInterface
    private interface RoleFactory {
        Role create(Player player);
    }

    private List<Player> allPlayers;
    private List<Role> aliveMembers;
    private boolean isDay;

    private static final List<RoleFactory> roles = List.of(
        Doctor::new,
        Citizen::new,
        Mafia::new,
        Citizen::new,
        Citizen::new,
        Mafia::new,
        Citizen::new,
        Policeman::new,
        Mafia::new,
        Citizen::new,
        Citizen::new
    );

    public Game(List<Player> players) {
        if (players.size() < 5) 
            throw new IllegalArgumentException("Not enough players, expected at least 5, but got " + players.size());
        
        this.allPlayers = players;
    }

    private void giveRoles() {
        Collections.shuffle(allPlayers);
        for (int i = 0; i < allPlayers.size(); i++)
            aliveMembers.add(roles.get(i).create(allPlayers.get(i)));
    }

    public void startGame() {
        giveRoles();
        isDay = false;
    }

    public void playDay() throws IncorrectGameTimeException {
        if (!isDay)
            throw new IncorrectGameTimeException(isDay);
    }

    public void playNight() throws IncorrectGameTimeException {
        if (isDay)
            throw new IncorrectGameTimeException(isDay);
    }


}
