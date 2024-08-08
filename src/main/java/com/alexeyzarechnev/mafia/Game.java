package com.alexeyzarechnev.mafia;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import com.alexeyzarechnev.mafia.exceptions.CharacterSelectionException;

import java.util.Set;
import java.util.HashSet;

public class Game {

    private final List<Player> allPlayers;
    private final Random random; 
    private final Host host;
    private Map<Player, Role> alivePlayers; 
    private Set<Player> killedPlayers;

    public Game(Host host, List<Player> players) {
        this.host = host;
        this.allPlayers = players;
        this.killedPlayers = new HashSet<Player>();
        this.random = new Random(System.currentTimeMillis());

        restart();   
    }

    public void restart() {
        RolesSet set = new RolesSet(allPlayers.size(), random);
        this.alivePlayers = allPlayers.stream()
               .collect(Collectors.toMap(p -> p, p -> set.getRole()));
    }

    public boolean isEnd() {
        long countOfMafia = alivePlayers.values().stream()
                            .filter(r -> r.equals(Role.MAFIA))
                            .count();
        return countOfMafia * 2 >= alivePlayers.size() || countOfMafia == 0;
    }

    private void round(Role role) {
        Action action = role.getAction();
        awake(role);
        try {
            doAction(action, host.getPlayerForAction(action));
        } catch (CharacterSelectionException e) {
            for (Map.Entry<Player, Role> entry : alivePlayers.entrySet()) {
                if (entry.getValue().equals(role)) {
                    entry.getKey().getMessage("You can't choose this player. Please, try again");
                }
            }
        }
        sleep(role);
    }

    public void playNight() {
        sleepAll();
        round(Role.MAFIA);
        round(Role.POLICEMAN);
        round(Role.DOCTOR);
        awakeAll();
    }

    public void playDay() {
        try {
            doAction(Action.KICK, host.getPlayerForAction(Action.KICK));
        } catch (CharacterSelectionException e) {
            host.getMessage("This player isn't alive. Please, try again");
            playDay();
        }
    }

    private void doAction(Action action, Player player) throws CharacterSelectionException {
        if (!alivePlayers.containsKey(player)) {
            throw new CharacterSelectionException();
        }
        switch(action) {
            case KILL:
                killedPlayers.add(player);
            case INVESTIGATE:
                String message = alivePlayers.get(player).equals(Role.MAFIA) ? "This player is Black" : "This player is Red";
                for (Map.Entry<Player, Role> entry : alivePlayers.entrySet()) {
                    if (entry.getValue().equals(Role.POLICEMAN)) {
                        entry.getKey().getMessage(message);
                    }
                }
            case HEAL:
                killedPlayers.remove(player);
            case KICK:
                alivePlayers.remove(player);
        }
    }

    private void sleep(Role role) {
        alivePlayers.forEach((p, r) -> {
            if (r.equals(role))
                p.sleep();
        });
    }

    private void sleepAll() {
        alivePlayers.forEach((p, r) -> { p.sleep(); });
    }

    private void awake(Role role) {
        alivePlayers.forEach((p, r) -> {
            if (r.equals(role))
                p.awake();
        });
    }

    private void awakeAll() {
        alivePlayers.forEach((p, r) -> { p.awake(); });
    }

    public Map<Player, Role> getAlivePlayers() {
        return alivePlayers;
    }
}
