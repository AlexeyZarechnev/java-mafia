package io.github.alexeyzarechnev.mafia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import io.github.alexeyzarechnev.mafia.exceptions.IncorrectGameTimeException;
import io.github.alexeyzarechnev.mafia.roles.*;

public class Game {

    @FunctionalInterface
    private interface RoleFactory {
        Role create(Player player);
    }

    private List<Player> allPlayers;
    private List<Role> aliveMembers;
    private boolean isDay;
    private Set<Player> injuredPlayers;

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

    private static final List<Class<? extends Role>> awakeOrder = List.of(
        Mafia.class,
        Policeman.class,
        Doctor.class
    ); 


    /**
     * Constructs a new Game object with the given list of players.
     * 
     * @param players the list of players participating in the game
     * @throws IllegalArgumentException if the number of players is less than 5
     */
    public Game(List<Player> players) {
        if (players.size() < 5) 
            throw new IllegalArgumentException("Not enough players, expected at least 5, but got " + players.size());
        
        this.allPlayers = players;
        this.aliveMembers = new ArrayList<>(players.size());
        this.injuredPlayers = new HashSet<>();
    }

    private void giveRoles() {
        aliveMembers.clear();
        Collections.shuffle(allPlayers);
        for (int i = 0; i < allPlayers.size(); i++)
            aliveMembers.add(roles.get(i).create(allPlayers.get(i)));
    }

    /**
     * Starts the game by assigning roles to players and setting the initial game state.
     */
    public void start() {
        giveRoles();
        isDay = false;
    }

    /**
     * Plays the day phase of the game.
     *
     * @throws IncorrectGameTimeException if the game time is not day.
     */
    public void playDay() throws IncorrectGameTimeException {
        if (!isDay)
            throw new IncorrectGameTimeException(isDay);
        
        Map<Player, Integer> votes = new HashMap<>();
        aliveMembers.forEach(member -> {
            Player vote = member.vote();
            votes.put(vote, votes.getOrDefault(vote, 0) + 1);
        });
        //TODO: переголосование, если у нескольких игроков одинаковое количество голосов
        kick(votes.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey());


        if (isEnd())
            endGame();

        isDay = false;
    }

    private void kick(Player player) { aliveMembers.removeIf(member -> member.getPlayer().equals(player)); }

    /**
     * Plays the night phase of the game.
     *
     * @throws IncorrectGameTimeException if called during the day phase.
     */
    public void playNight() throws IncorrectGameTimeException {
        if (isDay)
            throw new IncorrectGameTimeException(isDay);
        //TODO: реализовать метод, чтобы проходили все тесты (чтобы было с чем сравнить написал playDay)
    }

    private void sleep(Class<? extends Role> role) { 
        aliveMembers.forEach(member -> {
            if (member.getClass().equals(role))
                member.sleep();
            }); 
    }

    private void awake(Class<? extends Role> role) {
        aliveMembers.forEach(member -> {
            if (member.getClass().equals(role))
                member.awake();
            }); 
    }

    private void massiveSleep() { /*TODO: реализовать метод усыпления всех сразу */ }

    private void massiveAwake() { /*TODO: реализовать метод просыпания всех сразу */ }

    /**
     * Checks if the game has ended.
     *
     * @return true if the game has ended, false otherwise.
     */
    public boolean isEnd() {
        int blackCount = 0;
        for (Role member : aliveMembers)
            if (member.isBlack())
                blackCount++;
        return blackCount * 2 >= aliveMembers.size() || blackCount == 0;
    }

    private void endGame() { /*TODO: реализовать метод конца игры на твой вкус, для примера он может поздравлять победителей и не поздравлять проигравших */ }

    /**
     * Returns the number of remaining members in the game.
     *
     * @return the number of remaining members
     */
    public int remainedMembers() { return aliveMembers.size(); }

    /**
     * Returns a list of all alive players in the game.
     *
     * @return a list of alive players
     */
    public List<Player> getAlivePlayers() { return aliveMembers.stream().map(Role::getPlayer).toList(); }


    
    // Exclusive role methods

    /**
     * !! Exclusive method for the MAFIA role !!
     * Injures the specified player using the given weapon.
     *
     * @param player the player to be injured
     * @param weapon the weapon used to injure the player
     * @throws NullPointerException if the weapon is null
     */
    public void injure(Player player, Mafia.Weapon weapon) { 
        Objects.requireNonNull(weapon);
        injuredPlayers.add(player); 
    }

    /**
     * !! Exclusive method for the POLICEMAN role !!
     * Checks if a player is Black or not.
     *
     * @param player the player to check
     * @param archive the archive to search in
     * @return true if the player is present in the archive and is marked as black, false otherwise
     * @throws NullPointerException if the archive is null
     */
    public boolean check(Player player, Policeman.Archive archive) { 
        Objects.requireNonNull(archive);
        return aliveMembers.stream().anyMatch(member -> member.getPlayer().equals(player) && member.isBlack());
    } 

    /**
     * !! Exclusive method for the DOCTOR role !!
     * Heals the specified player using the given medicine.
     *
     * @param player the player to heal
     * @param medicine the medicine to use for healing
     * @throws NullPointerException if the medicine is null
     */
    public void heal(Player player, Doctor.Medicine medicine) { 
        Objects.requireNonNull(medicine);
        injuredPlayers.remove(player); 
    }
     

}
