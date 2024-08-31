package io.github.alexeyzarechnev.mafia;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.alexeyzarechnev.mafia.roles.Mafia;

public class GameTests {

    private static class TestPlayer implements Player {

        public Player vote;
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
        return players;
    }

    @Test
    public void isEndTest() {
        init(5);
        game.start();
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
            System.out.println(newMembers);
            field.set(game, newMembers);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        assertTrue(game.isEnd());
    }
}
