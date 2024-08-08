package com.alexeyzarechnev.mafia;

import java.util.ArrayList;
import java.util.List;

public enum Role {
    MAFIA(Action.KILL),
    CITIZEN(null),
    POLICEMAN(Action.INVESTIGATE),
    DOCTOR(Action.HEAL);
    // TODO: SHERIFF, HARLOT, WEREWOLF, KAMIKAZE, MANIAC

    private final Action action;

    private Role(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    private static List<Role> rolesSet = List.of(
        CITIZEN,
        CITIZEN,
        CITIZEN,
        MAFIA,
        POLICEMAN,
        MAFIA,
        DOCTOR,
        CITIZEN,
        MAFIA,
        CITIZEN,
        CITIZEN
        //TODO: add more roles for more players
    );
    
    public static List<Role> getRoles(int count) {
        return new ArrayList<Role>(rolesSet.subList(0, count));
    }
}
