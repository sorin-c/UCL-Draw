package com.ucldraw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.ucldraw.FootballTeam.newTeam;

public class UclDrawProcess {

    private final Logger log = LoggerFactory.getLogger(UclDrawProcess.class);

    private Set<FootballTeam> seededTeams;
    private Set<FootballTeam> unseededTeams;

    public UclDrawProcess() {
        addSeededTeams();
        addUnseededTeams();
    }

    private void addSeededTeams() {
        seededTeams = new HashSet<>();
        seededTeams.add(newTeam("RM", "Real Madrid", "Spain", "D"));
        seededTeams.add(newTeam("MC", "Manchester City", "England", "A"));
        seededTeams.add(newTeam("BM", "Bayern Munich", "Germany", "E"));
        seededTeams.add(newTeam("LIV", "Liverpool", "England", "B"));
        seededTeams.add(newTeam("LIL", "LOSC Lille", "France", "G"));
        seededTeams.add(newTeam("JUV", "Juventus", "Italy", "H"));
        seededTeams.add(newTeam("MU", "Manchester United", "England", "F"));
        seededTeams.add(newTeam("AJX", "Ajax Amsterdam", "Netherlands", "C"));
    }


    private void addUnseededTeams() {
        unseededTeams = new HashSet<>();
        unseededTeams.add(newTeam("PSG", "Paris Saint Germain", "France", "A"));
        unseededTeams.add(newTeam("SP", "Sporting", "Portugal", "C"));
        unseededTeams.add(newTeam("RBS", "RB Salzburg", "Austria", "G"));
        unseededTeams.add(newTeam("IM", "Inter Milan", "Italy", "D"));
        unseededTeams.add(newTeam("CHE", "Chelsea", "England", "H"));
        unseededTeams.add(newTeam("VIL", "Villarreal", "Spain", "F"));
        unseededTeams.add(newTeam("AM", "Atletico Madrid", "Spain", "B"));
        unseededTeams.add(newTeam("BEN", "Benfica", "Portugal", "E"));
    }


    public Set<FootballTeam> getSeededTeams() {
        return seededTeams;
    }


    public Set<FootballTeam> getUnseededTeams() {
        return unseededTeams;
    }


    public List<FootballMatch> performDraw(DrawType drawType, List<String> drawCodes) {

        List<FootballMatch> matches = new ArrayList<>();

        int drawIndex = 0;

        if (!drawCodes.isEmpty() && drawCodes.size() != unseededTeams.size() + seededTeams.size()) {
            throw new IllegalArgumentException("Number of provided draw codes must be equal to the number of teams (" + Math.addExact(unseededTeams.size(), seededTeams.size()) + ")");
        }

        while (!seededTeams.isEmpty() && !unseededTeams.isEmpty()) {

            FootballTeam nextUnseededTeam = switch (drawType) {
                case AUTOMATIC -> getRandomTeam(unseededTeams);
                case MANUAL_KEY_INPUT -> manuallySelectTeam(unseededTeams, TeamSelectionType.UNSEEDED);
                case MANUAL_CODES -> selectTeamByCode(unseededTeams, TeamSelectionType.UNSEEDED, drawCodes.get(drawIndex++));
            };

            unseededTeams.remove(nextUnseededTeam);
            log.info("Next unseeded team: " + nextUnseededTeam);

            log.info("All remaining seeded teams: " + seededTeams);
            Set<FootballTeam> potentialSeededOpponents = getSeededOpponents(nextUnseededTeam, unseededTeams, seededTeams, true)
                    .orElseThrow(() -> new RuntimeException("No potential seeded teams found"));
            log.info("Potential seeded opponents: " + potentialSeededOpponents);


            FootballTeam nextSeededTeam = switch (drawType) {
                case AUTOMATIC -> getRandomTeam(potentialSeededOpponents);
                case MANUAL_KEY_INPUT -> manuallySelectTeam(potentialSeededOpponents, TeamSelectionType.SEEDED);
                case MANUAL_CODES -> selectTeamByCode(potentialSeededOpponents, TeamSelectionType.SEEDED, drawCodes.get(drawIndex++));
            };

            seededTeams.remove(nextSeededTeam);
            log.info(nextUnseededTeam + " - " + nextSeededTeam);

            matches.add(new FootballMatch(nextUnseededTeam, nextSeededTeam, potentialSeededOpponents));

            log.info("-----------------------------------------------------------------");

        }

        log.info("Draw result:");
        for (FootballMatch match : matches) {
            log.info(match.toString());
        }

        return matches;

    }


    private FootballTeam selectTeamByCode(Set<FootballTeam> teams, TeamSelectionType teamSelectionType, String teamCode) {

        FootballTeam selectedTeam = teams.stream().filter(t -> t.code().equals(teamCode)).findAny()
                .orElseThrow(() -> new RuntimeException("Unable to find team by code " + teamCode));

        log.info(selectedTeam + " has been selected.");

        return selectedTeam;
    }


    private FootballTeam manuallySelectTeam(Set<FootballTeam> teams, TeamSelectionType teamSelectionType) {

        System.out.println("Please select one of the following " + teamSelectionType + " teams: ");
        int i = 1;
        for (FootballTeam team : teams) {
            System.out.println(i + ". " + team);
            i++;
        }

        Scanner keyboard = new Scanner(System.in);
        int selectedTeam = keyboard.nextInt();
        System.out.println(selectedTeam + " has been entered.");
        while (selectedTeam < 1 || selectedTeam > teams.size()) {
            System.out.println("Invalid selection. Please enter a number between 1 and " + teams.size() + ".");
            selectedTeam = keyboard.nextInt();
        }

        FootballTeam manuallySelectedTeam = new ArrayList<>(teams).get(selectedTeam - 1);

        System.out.println(manuallySelectedTeam + " has been selected.");

        return manuallySelectedTeam;
    }


    private Optional<Set<FootballTeam>> getSeededOpponents(FootballTeam nextUnseededTeam, Set<FootballTeam> remainingUnseededTeams, Set<FootballTeam> seededTeams, boolean checkForDrawDeadlock) {

        Set<FootballTeam> potentialSeededOpponents = new HashSet<>();

        for (FootballTeam seededTeam : seededTeams) {
            if (!seededTeam.country().equals(nextUnseededTeam.country()) && !seededTeam.group().equals(nextUnseededTeam.group())) {
                potentialSeededOpponents.add(seededTeam);
            } else {
                if (checkForDrawDeadlock) logRemovedTeam(nextUnseededTeam, seededTeam);
            }
        }

        return Optional.of(checkForDrawDeadlock ? removeSeededOpponentsToPreventDrawDeadlock(remainingUnseededTeams, potentialSeededOpponents, seededTeams) : potentialSeededOpponents);
    }


    private void logRemovedTeam(FootballTeam nextUnseededTeam, FootballTeam seededTeam) {
        if (seededTeam.country().equals(nextUnseededTeam.country()) && seededTeam.group().equals(nextUnseededTeam.group())) {
            log.info("Removed " + seededTeam.name() + " - same country (" + seededTeam.country() + ") and group (" + seededTeam.group() + ")");
        } else if (seededTeam.country().equals(nextUnseededTeam.country())) {
            log.info("Removed " + seededTeam.name() + " - same country (" + seededTeam.country() + ")");
        } else if (seededTeam.group().equals(nextUnseededTeam.group())) {
            log.info("Removed " + seededTeam.name() + " - same group (" + seededTeam.group() + ")");
        }
    }


    /**
     * Any seeded teams that could cause the draw to result in a deadlock will be removed.
     */
    private Set<FootballTeam> removeSeededOpponentsToPreventDrawDeadlock(Set<FootballTeam> remainingUnseededTeams, Set<FootballTeam> potentialSeededOpponents, Set<FootballTeam> seededTeams) {

        HashSet<FootballTeam> potentialSeededOpponentsCopy = new HashSet<>(potentialSeededOpponents);
        HashSet<FootballTeam> seededTeamsCopy = new HashSet<>(seededTeams);

        for (FootballTeam potentialSeededOpponent : potentialSeededOpponents) {
            // Remove the team to check if picking it would result in a deadlock
            seededTeamsCopy.remove(potentialSeededOpponent);
            // If the draw does not result in a deadlock, re-add the team
            if (checkForDrawDeadlock(remainingUnseededTeams, seededTeamsCopy)) {
                potentialSeededOpponentsCopy.remove(potentialSeededOpponent);
                log.info("Removed " + potentialSeededOpponent.name() + " - to prevent draw deadlock");
            }
            seededTeamsCopy.add(potentialSeededOpponent);
        }

        return potentialSeededOpponentsCopy;

    }


    /**
     * Returns true if the given set of teams can result in a deadlock, which means
     * that the draw cannot be finalized, so no potential seeded opponents can be found.
     */
    private boolean checkForDrawDeadlock(Set<FootballTeam> unseededTeamsCopy, Set<FootballTeam> seededTeamsCopy) {

        loop:
        for (int i = 0; i < 100; i++) { // instead of this, we need to check all the possible permutations

            HashSet<FootballTeam> unseededTeams = new HashSet<>(unseededTeamsCopy);
            HashSet<FootballTeam> seededTeams = new HashSet<>(seededTeamsCopy);

            while (!seededTeams.isEmpty() && !unseededTeams.isEmpty()) {

                FootballTeam nextUnseededTeam = getRandomTeam(unseededTeams);
                unseededTeams.remove(nextUnseededTeam);

                Optional<Set<FootballTeam>> potentialSeededOpponents = getSeededOpponents(nextUnseededTeam, unseededTeams, seededTeams, false);
                if (potentialSeededOpponents.isEmpty() || potentialSeededOpponents.get().isEmpty()) {
                    continue loop;
                }

                FootballTeam nextSeededTeam = getRandomTeam(potentialSeededOpponents.get());
                seededTeams.remove(nextSeededTeam);

            }

            return false;

        }

        return true;

    }


    private FootballTeam getRandomTeam(Set<FootballTeam> teams) {

        int item = new Random().nextInt(teams.size());

        int i = 0;
        for(FootballTeam team : teams) {
            if (i == item) {
                return team;
            }
            i++;
        }

        return null;

    }


    enum DrawType {
        MANUAL_CODES,
        MANUAL_KEY_INPUT,
        AUTOMATIC
    }

    enum TeamSelectionType {
        SEEDED("seeded"),
        UNSEEDED("unseeded");

        private final String type;

        TeamSelectionType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

}
