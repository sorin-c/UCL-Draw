package com.ucldraw;

import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.ucldraw.UclDrawProcess.DrawType.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class TestUclDrawProcess {

    private UclDrawProcess uclDrawProcess;
    private Set<FootballTeam> allTeams;

    @BeforeEach
    public void setup() {
        uclDrawProcess = new UclDrawProcess();
        allTeams = Sets.newHashSet(uclDrawProcess.getSeededTeams());
        allTeams.addAll(uclDrawProcess.getUnseededTeams());
    }

    @Test
    public void testPerformManualSecondDraw() {

        List<String> teamCodes = new ArrayList<>();
        teamCodes.add("RBS");
        teamCodes.add("BM");
        teamCodes.add("SP");
        teamCodes.add("MC");
        teamCodes.add("BEN");
        teamCodes.add("AJX");
        teamCodes.add("CHE");
        teamCodes.add("LIL");
        teamCodes.add("AM");
        teamCodes.add("MU");
        teamCodes.add("VIL");
        teamCodes.add("JUV");
        teamCodes.add("IM");
        teamCodes.add("LIV");
        teamCodes.add("PSG");
        teamCodes.add("RM");


        List<FootballMatch> matches = uclDrawProcess.performDraw(MANUAL_CODES, teamCodes);

        assertThat(matches, containsInAnyOrder(
                new FootballMatch(getTeamByCode( "RBS"), getTeamByCode( "BM"), getTeamsByCodes("MC", "BM", "LIV", "MU", "AJX", "RM", "JUV")),
                new FootballMatch(getTeamByCode( "SP"), getTeamByCode("MC"), getTeamsByCodes("MC", "LIV", "MU", "LIL", "RM", "JUV")),
                new FootballMatch(getTeamByCode( "BEN"), getTeamByCode( "AJX"), getTeamsByCodes("LIV", "MU", "AJX", "LIL", "RM", "JUV")),
                new FootballMatch(getTeamByCode( "CHE"), getTeamByCode( "LIL"), getTeamsByCodes("LIL", "RM")),
                new FootballMatch(getTeamByCode( "AM"), getTeamByCode("MU"), getTeamsByCodes("MU", "JUV")),
                new FootballMatch(getTeamByCode( "VIL"), getTeamByCode( "JUV"), getTeamsByCodes( "JUV")), // LIV removed here because of draw deadlock - see draw 8:50
                new FootballMatch(getTeamByCode( "IM"), getTeamByCode("LIV"), getTeamsByCodes( "LIV")),
                new FootballMatch(getTeamByCode( "PSG"), getTeamByCode( "RM"), getTeamsByCodes( "RM"))));

    }


    private FootballTeam getTeamByCode(String teamCode) {
        return allTeams.stream()
                .filter(team -> team.code().equals(teamCode))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }


    private Collection<FootballTeam> getTeamsByCodes(String... teamCodes) {
        return Arrays.stream(teamCodes)
                .map(this::getTeamByCode)
                .collect(Collectors.toSet());
    }

}
