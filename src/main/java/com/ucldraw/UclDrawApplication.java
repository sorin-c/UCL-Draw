package com.ucldraw;

//import jfdata.manager.JfdataManager;
//import jfdata.model.competition.CompetitionList;
//import jfdata.model.team.Team;
//import jfdata.model.team.TeamList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

import static com.ucldraw.UclDrawProcess.DrawType.*;

@SpringBootApplication
public class UclDrawApplication {

	private Logger log = LoggerFactory.getLogger(UclDrawApplication.class);

	private static final int UCL_ID = 2001;

	public static void main(String[] args) {

		SpringApplication.run(UclDrawApplication.class, args);

		UclDrawProcess uclDrawProcess = new UclDrawProcess();

//		System.out.println("Rules");
//		System.out.println("No team could play a club from their group or any side from their own association.");
//		System.out.println("During the draw, teams must not be places in the pot if it will result in the remaining teams not being able to be drawn against each other.");

//		fetchTeamData();

//		System.out.println("Teams");
//		System.out.println(seededTeams);
//		System.out.println();
//		System.out.println(unseededTeams);
//		System.out.println();

		uclDrawProcess.performDraw(MANUAL_CODES, Collections.emptyList());


	}





//	private static void fetchTeamData() {
//		JfdataManager jfdataManager = new JfdataManager("e1214d448af44ec3bb383cf81519dbb3");
//
//		CompetitionList competitions = jfdataManager.getAllCompetitions();
//
//		TeamList teamsByCompetition = jfdataManager.getTeamsByCompetition(UCL_ID);
//
//		for (Team team : teamsByCompetition.getTeams()) {
//			System.out.println(team);
//			System.out.println();
//		}
//
////		for (Competition competition : competitions.getCompetitions()) {
////			System.out.println(competition);
////			System.out.println();
////		}
//	}

}
