package com.match_service.service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.match_service.dto.FootballDataMatchResponse;
import com.match_service.dto.FootballDataMatchesResponse;
import com.match_service.model.Match;
import com.match_service.model.StatusType;
import com.match_service.repository.MatchRepo;

@Service
public class MatchService {
	@Autowired
    private MatchRepo matchRepository;
	@Autowired
	private RestClient footballDataRestClient;
	
    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    public Match getMatchById(Long id) {
        return matchRepository.findById(id)
                .orElse(null);
    }

    public Match saveMatch(Match match) {
        return matchRepository.save(match);
    }
    
    public List<Match> getMatchesByCompetition(String competition) {
        return matchRepository.findByCompetition(competition);
    }
    
    public int syncLaLigaMatches(Integer season) {

        FootballDataMatchesResponse response = footballDataRestClient.get().uri(uriBuilder -> uriBuilder
								                .path("/competitions/PD/matches")
								                .queryParam("season", season)
								                .build())
        										.retrieve()
        										.body(FootballDataMatchesResponse.class);

        if (response == null || response.matches() == null) {
            throw new IllegalStateException("No se han recibido partidos de football-data.org");
        }

        for (FootballDataMatchResponse externalMatch : response.matches()) {
        	Match match = matchRepository.findByExternalId(externalMatch.id()).orElseGet(Match::new);

            match.setExternalId(externalMatch.id());
            match.setHomeTeam(externalMatch.homeTeam().name());
            match.setAwayTeam(externalMatch.awayTeam().name());
            match.setMatchDay(externalMatch.matchday());
            
            System.out.println(
            	    "Partido: " + externalMatch.homeTeam().name()
            	    + " - " + externalMatch.awayTeam().name()
            	    + " | Jornada: " + externalMatch.matchday()
            	);

            LocalDateTime dateTime = OffsetDateTime.parse(externalMatch.utcDate()).toLocalDateTime();

            match.setDateTime(dateTime);
            match.setCompetition("LaLiga");
            match.setStatus(mapStatus(externalMatch.status()));

            if (externalMatch.score() != null && externalMatch.score().fullTime() != null) {

                match.setHomeScore(externalMatch.score().fullTime().home());

                match.setAwayScore(externalMatch.score().fullTime().away());
            } else {
                match.setHomeScore(null);
                match.setAwayScore(null);
            }
            matchRepository.save(match);
        }
        return response.matches().size();
    }
    
    private StatusType mapStatus(String externalStatus) {
        return switch (externalStatus) {
            case "SCHEDULED" -> StatusType.SCHEDULED;
            case "IN_PLAY" -> StatusType.IN_PLAY;
            case "LIVE" -> StatusType.LIVE;
            case "PAUSED" -> StatusType.PAUSED;
            case "POSTPONED" -> StatusType.POSTPONED;
            case "SUSPENDED" -> StatusType.SUSPENDED;
            case "FINISHED" -> StatusType.FINISHED;
            case "CANCELLED" -> StatusType.CANCELLED;
            case "TIMED" -> StatusType.TIMED;
            case "AWARDED" -> StatusType.AWARDED;

            default -> throw new IllegalArgumentException("Estado externo no reconocido: " + externalStatus);
        };
    }
}