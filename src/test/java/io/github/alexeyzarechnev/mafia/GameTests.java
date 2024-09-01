package io.github.alexeyzarechnev.mafia;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.alexeyzarechnev.mafia.exceptions.IncorrectGameTimeException;
import io.github.alexeyzarechnev.mafia.roles.Citizen;
import io.github.alexeyzarechnev.mafia.roles.Mafia;

public class GameTests {

    private static class TestPlayer implements Player {

        public Player vote = null;
        public int sleepCount = 0;
        public int awakeCount = 0;

        @Override
        public void sleep() { ++sleepCount; }

        @Override
        public void awake() { ++awakeCount; }

        @Override
        public Player vote() { return vote; }

    }

    @Test
    public void creationTest() { 
        List<Player> players = List.of(new TestPlayer(), new TestPlayer(), new TestPlayer(), new TestPlayer(), new TestPlayer());
        assertDoesNotThrow(() -> new Game(players));    
        assertThrows(IllegalArgumentException.class, () -> new Game(players.subList(1, 5)));    
        assertThrows(IllegalArgumentException.class, () -> new Game(players.subList(2, 2)));    
    }

    private Game game;

    private List<Player> init(int count) {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < count; i++)
            players.add(new TestPlayer());
        
        game = new Game(players);
        game.start();
        return players;
    }

    @Test
    public void isEndTest() {
        init(5);
        assertFalse(game.isEnd());
        try {
            Field field = game.getClass().getDeclaredField("aliveMembers");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Role> members = List.copyOf((List<Role>) field.get(game));
            List<Role> newMembers = new ArrayList<>(members);
            for (Role member : members) {
                if (member.getClass().equals(Mafia.class))
                    newMembers.remove(member);
            }
            field.set(game, newMembers);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        assertTrue(game.isEnd());
    }

    @Test
    public void nightWithoutKillTest() {
        init(5);
        assertDoesNotThrow(() -> game.playNight());
        assertEquals(5, game.remainedMembers());
    }


    @Test
    public void nightWithKillTest() {
        List<Player> players = init(5);
        players.forEach(player -> ((TestPlayer) player).vote = players.get(0));
        assertDoesNotThrow(() -> game.playNight());
        assertEquals(4, game.remainedMembers());
    }

    @Test
    public void differentMafiaVotesTest() {
        init(9);
        try {
            Field field = game.getClass().getDeclaredField("aliveMembers");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Role> members = List.copyOf((List<Role>) field.get(game));
            int i = 0;
            for (Role member : members) {
                if (member.getClass().equals(Mafia.class)) {
                    ((TestPlayer) member.getPlayer()).vote = members.get(i < 2 ? 0 : 1).getPlayer();
                    ++i;
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        assertDoesNotThrow(() -> game.playNight());
        assertEquals(8, game.remainedMembers());
    }

    @Test
    public void drawMafiaVotesTest() {
        init(6);
        try {
            Field field = game.getClass().getDeclaredField("aliveMembers");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Role> members = List.copyOf((List<Role>) field.get(game));
            int i = 0;
            for (Role member : members) {
                if (member.getClass().equals(Mafia.class)) {
                    ((TestPlayer) member.getPlayer()).vote = members.get(i < 1 ? 0 : 1).getPlayer();
                    ++i;
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        assertDoesNotThrow(() -> game.playNight());
        assertEquals(6, game.remainedMembers());
    }

    @Test
    public void doubleNightTest() {
        init(5);
        assertDoesNotThrow(() -> game.playNight());
        assertThrows(IncorrectGameTimeException.class, () -> game.playNight());
    }

    @Test
    public void countTest() {
        init(5);
        assertDoesNotThrow(() -> game.playNight());
        try {
            Field field = game.getClass().getDeclaredField("aliveMembers");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Role> members = List.copyOf((List<Role>) field.get(game));
            for (Role member : members) {
                if (member.getClass().equals(Citizen.class)) {
                    assertEquals(1, ((TestPlayer) member.getPlayer()).sleepCount);
                    assertEquals(1, ((TestPlayer) member.getPlayer()).awakeCount);
                } else {
                    assertEquals(2, ((TestPlayer) member.getPlayer()).sleepCount);
                    assertEquals(2, ((TestPlayer) member.getPlayer()).awakeCount);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void setDay() {
        try {
            Field field = game.getClass().getDeclaredField("isDay");
            field.setAccessible(true);
            field.set(game, true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void dayWithoutVoteTest() {
        init(5);
        setDay();
        assertDoesNotThrow(() -> game.playDay());
        assertEquals(5, game.remainedMembers());
    }

    @Test 
    public void dayWithVoteTest() {
        List<Player> players = init(5);
        setDay();
        players.forEach(player -> ((TestPlayer) player).vote = players.get(0));
        assertDoesNotThrow(() -> game.playDay());
        assertEquals(4, game.remainedMembers());
    }

    @Test
    public void invalidDayTest() {
        init(5);
        assertThrows(IncorrectGameTimeException.class, () -> game.playDay());
    }
}
