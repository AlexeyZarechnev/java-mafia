package io.github.alexeyzarechnev.mafia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import static java.util.Map.entry;

import io.github.alexeyzarechnev.mafia.exceptions.IncorrectGameTimeException;
import io.github.alexeyzarechnev.mafia.mechanics.Vote;
import io.github.alexeyzarechnev.mafia.roles.*;

public class Game {

    @FunctionalInterface
    private interface RoleFactory {
        Role create(Player player);
    }

    private final List<Player> allPlayers;
    private final List<Role> aliveMembers;
    private boolean isDay;
    private final Set<Player> injuredPlayers;

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

    private static final Map<Class<? extends Role>, BiFunction<Game, Player, Boolean>> nightActions = Map.ofEntries(
        entry(Mafia.class, Game::injure),
        entry(Policeman.class, Game::check),
        entry(Doctor.class, Game::heal)
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

        Vote vote = new Vote(aliveMembers.stream().map(Role::getPlayer).toList(), aliveMembers.stream().map(Role::getPlayer).toList());
        kick(vote.getResult());

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

        massiveSleep();
        
        nightActions.entrySet().forEach(role -> {
            final List<Role> awakens = new ArrayList<>();
            aliveMembers.forEach(member -> {
                if (member.getClass().equals(role.getKey())) {
                    member.awake();
                    awakens.add(member);
                }
            });
            Vote vote = new Vote(awakens.stream().map(Role::getPlayer).toList(), aliveMembers.stream().filter(awakens::contains).map(Role::getPlayer).toList());
            role.getValue().apply(this, vote.getResult());
            awakens.forEach(Role::sleep);
        });
        injuredPlayers.forEach(this::kick);
        injuredPlayers.clear();
        massiveAwake();

        if (isEnd())
            endGame();

        isDay = true;
    }

    @SuppressWarnings("unused")
    private void sleep(Class<? extends Role> role) { 
        aliveMembers.forEach(member -> {
            if (member.getClass().equals(role))
                member.sleep();
            }); 
    }

    @SuppressWarnings("unused")
    private void awake(Class<? extends Role> role) {
        aliveMembers.forEach(member -> {
            if (member.getClass().equals(role))
                member.awake();
            }); 
    }

    @SuppressWarnings("unused")
    private void massiveSleep() { aliveMembers.forEach(Role::sleep); }

    @SuppressWarnings("unused")
    private void massiveAwake() { aliveMembers.forEach(Role::awake); }

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

    private void endGame() { 
        int blackCount = 0;
        for (Role member : aliveMembers)
            if (member.isBlack())
                blackCount++;
        for (Role member : aliveMembers)
            if (member.isBlack())
                member.win(blackCount > 0 ? member.isBlack() : !member.isBlack());
    }

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

    
    // action methods

    /**
     * !! Exclusive method for the MAFIA role !!
     * Injures the specified player using the given weapon.
     *
     * @param player the player to be injured
     * @throws NullPointerException if the weapon is null
     */
    public boolean injure(Player player) { 
        injuredPlayers.add(player); 
        return true;
    }

    /**
     * !! Exclusive method for the POLICEMAN role !!
     * Checks if a player is Black or not.
     *
     * @param player the player to check
     * @return true if the player is present in the archive and is marked as black, false otherwise
     * @throws NullPointerException if the archive is null
     */
    public boolean check(Player player) { 
        return aliveMembers.stream().anyMatch(member -> member.getPlayer().equals(player) && member.isBlack());
    } 

    /**
     * !! Exclusive method for the DOCTOR role !!
     * Heals the specified player using the given medicine.
     *
     * @param player the player to heal
     * @throws NullPointerException if the medicine is null
     */
    public boolean heal(Player player) {
        return injuredPlayers.remove(player);
    }
     

}
