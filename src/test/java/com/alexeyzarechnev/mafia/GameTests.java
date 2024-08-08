package com.alexeyzarechnev.mafia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.alexeyzarechnev.mafia.exceptions.InvalidTimeException;

public class GameTests {

    public static class TestPlayer implements Player {

        private int awakeCount = 0;
        private int sleepCount = 0;

        @Override
        public void awake() {
            ++awakeCount;
        }

        @Override
        public void sleep() {
            ++sleepCount;
        }

        @Override
        public void getMessage(String message) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getMessage'");
        }
    }

    public static class TestHost implements Host {

        private static Player killed;
        private static Player checked;
        private static Player healed;
        private static Player voted;

        @Override
        public Player getPlayerForAction(Action action) {
            Player result;
            switch (action) {
                case KILL -> result = killed;
                case INVESTIGATE-> result = checked;
                case HEAL -> result = healed;
                default -> result = voted;
            }
            return result;
        }

        @Override
        public void getMessage(String message) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getMessage'");
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5, 6, 7, 8})
    public void startTest(int count) {
        List<Player> players = new ArrayList<Player>();
        for (int i = 0; i < count; i++)
            players.add(new TestPlayer());

        Game game = new Game(new TestHost(), players);
        Map<Player, Role> map = game.getAlivePlayers();
        assertEquals(count, map.size());
        if (count < 4)
            assertTrue(game.isEnd());
        else 
            assertFalse(game.isEnd());
    }

    private Game game;

    private List<Player> init(int count) {
        List<Player> players = new ArrayList<Player>();
        for (int i = 0; i < count; i++) { 
            players.add(new TestPlayer()); 
        }
        game = new Game(new TestHost(), players);
        return players;
    }

    private Player findPlayer(Role role) {
        Map<Player, Role> map = game.getAlivePlayers();
        for (Map.Entry<Player, Role> e : map.entrySet())
            if (e.getValue().equals(role)) {
                return e.getKey();
            }

        return null;
    }

    @Test
    public void nightWithoutKillTest() {
        init(5);
        TestHost.healed = TestHost.checked = TestHost.killed = findPlayer(Role.MAFIA);

        game.playNight();

        Map<Player, Role> map = game.getAlivePlayers();
        map.forEach((p, r) -> {
            TestPlayer tp = (TestPlayer)p;
            if (r.equals(Role.CITIZEN)) {
                assertEquals(1, tp.awakeCount);
                assertEquals(1, tp.sleepCount);
            } else {
                assertEquals(2, tp.awakeCount);
                assertEquals(2, tp.sleepCount);
            }
        });
        assertEquals(5, map.size());
        assertTrue(map.containsKey(TestHost.killed));
        assertFalse(game.isEnd());
    }

    @Test
    public void nightWithKillTest() {
        init(5);
        TestHost.healed = TestHost.checked = null;
        TestHost.killed = findPlayer(Role.MAFIA);

        game.playNight();

        Map<Player, Role> map = game.getAlivePlayers();
        map.forEach((p, r) -> {
            TestPlayer tp = (TestPlayer)p;
             if (r.equals(Role.MAFIA)) {
                assertEquals(1, tp.awakeCount);
                assertEquals(2, tp.sleepCount);
            }
        });
        assertEquals(5, map.size());
        assertFalse(map.containsKey(TestHost.killed));
        assertTrue(game.isEnd());
    }

    @Test
    public void doubleDayTest() {
        init(6);
        assertThrows(InvalidTimeException.class, () -> game.playDay());
    }

    @Test
    public void doubleNightTest() {
        init(6);
        TestHost.healed = TestHost.checked = TestHost.killed = null;

        game.playNight();
        assertThrows(InvalidTimeException.class, () -> game.playNight());

    }

    @Test
    public void dayTest() {
        init(6);
        TestHost.healed = TestHost.checked = TestHost.killed = null;
        TestHost.voted = findPlayer(Role.DOCTOR);

        game.playNight();
        game.playDay();

        Map<Player, Role> map = game.getAlivePlayers();
        assertEquals(5, map.size());
        assertFalse(map.containsKey(TestHost.voted));
        assertFalse(game.isEnd());
    }

    @Test
    public void lastDayTest() {
        init(6);
        TestHost.healed = TestHost.checked = TestHost.killed = null;
        TestHost.voted = findPlayer(Role.MAFIA);
        
        game.playNight();
        game.playDay();

        assertTrue(game.isEnd());
    }

    @Test
    public void killedNotAwaking() {
        init(5);
        TestHost.voted = TestHost.healed = TestHost.checked = null;
        TestHost.killed = findPlayer(Role.DOCTOR);

        game.playNight();
        game.playDay();
        TestHost.killed = null;
        game.playNight();

        Map<Player, Role> map = game.getAlivePlayers();
        map.forEach((p, r) -> {
            TestPlayer tp = (TestPlayer)p;
             if (r.equals(Role.DOCTOR)) {
                assertEquals(1, tp.awakeCount);
                assertEquals(2, tp.sleepCount);
            }
        });
    }

    @Test
    public void restartTest() {
        init(5);
        Map<Player, Role> map1 = game.getAlivePlayers();

        game.restart();

        Map<Player, Role> map2 = game.getAlivePlayers();
        assertNotEquals(map1, map2);

    }
}
