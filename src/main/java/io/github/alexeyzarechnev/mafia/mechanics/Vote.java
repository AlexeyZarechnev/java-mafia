package io.github.alexeyzarechnev.mafia.mechanics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.alexeyzarechnev.mafia.Player;

public class Vote {

    private final List<Player> playersWhoVote;
    private Set<Player> playersForVote;

    public Vote(List<Player> playersWhoVote, List<Player> playersForVote) {
        this.playersWhoVote = playersWhoVote;
        this.playersForVote = new HashSet<>(playersForVote);
    }

    public Player getResult() {
        Map<Player, Integer> votes = new HashMap<>();
        while (true) {
            votes.clear();
            playersWhoVote.forEach(member -> {
                Player vote = member.vote(playersForVote.stream().toList());
                votes.put(vote, votes.getOrDefault(vote, 0) + 1);
            });
            int maxCount = votes.entrySet().stream().mapToInt(Map.Entry::getValue).max().orElse(0);
            List<Player> mostVoted = votes.entrySet().stream()
                    .filter(entry -> entry.getValue() == maxCount)
                    .map(Map.Entry::getKey)
                    .toList();
            if (mostVoted.size() == 0) {
                return null;
            } else if (mostVoted.size() == 1) {
                return mostVoted.get(0);
            } else {
                playersForVote.clear();
                playersForVote.addAll(mostVoted);
                playersWhoVote.forEach(player -> player.message("vote again"));
            }
        }
    }
}
