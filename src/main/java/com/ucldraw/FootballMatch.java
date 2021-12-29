package com.ucldraw;

import java.util.Collection;

public record FootballMatch(FootballTeam unseededTeam, FootballTeam seededTeam, Collection<FootballTeam> potentialSeededOpponents) {

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(unseededTeam + " - " + seededTeam + " [ ");
        for (FootballTeam potentialSeededOpponent : potentialSeededOpponents) {
            result.append(potentialSeededOpponent.code()).append(" ");
        }
        result.append("]");
        return result.toString();
    }
}
