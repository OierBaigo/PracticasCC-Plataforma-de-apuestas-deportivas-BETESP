package com.match_service.service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.match_service.client.SettlementServiceClient;
import com.match_service.dto.FootballDataMatchResponse;
import com.match_service.dto.FootballDataMatchesResponse;
import com.match_service.dto.MatchResponse;
import com.match_service.exception.MatchNotExistsException;
import com.match_service.model.Match;
import com.match_service.model.StatusType;
import com.match_service.repository.MatchRepo;
import com.match_service.service.MatchService;

@Service
public class MatchService {
	private final MatchRepo matchRepository;
	private final RestClient footballDataRestClient;
	private final SettlementServiceClient settlementServiceClient;
	
	private static final Logger log = LoggerFactory.getLogger(MatchService.class);

    MatchService(MatchRepo matchRepository, RestClient footballDataRestClient, SettlementServiceClient settlementServiceClient) {
        this.matchRepository = matchRepository;
        this.footballDataRestClient = footballDataRestClient;
        this.settlementServiceClient = settlementServiceClient;
    }
	
    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    public Match getMatchById(Long id) {
    	Match match = matchRepository.findById(id).orElseThrow(() ->
								new MatchNotExistsException("Partido no encontrado"));
        return match;
    }

    public Match saveMatch(Match match) {
        return matchRepository.save(match);
    }
    
    public List<Match> getMatchesByCompetition(String competition) {
        return matchRepository.findByCompetition(competition);
    }
    
    public int syncLaLigaMatches(Integer season) {
    	FootballDataMatchesResponse response;
        try { 
        	response = footballDataRestClient.get().uri(uriBuilder -> uriBuilder
	                .path("/competitions/PD/matches")
	                .queryParam("season", season)
	                .build())
					.retrieve()
					.body(FootballDataMatchesResponse.class);
        } catch(RestClientResponseException ex) {
        	throw ex;
        }
								                

        if (isResponseNull(response)) {
            throw new IllegalStateException("No se han recibido partidos de football-data.org");
        }

        for (FootballDataMatchResponse externalMatch : response.matches()) {
        	Optional<Match> existingMatch = matchRepository.findByExternalId(externalMatch.id());

        	boolean newlyFinished = existingMatch.isPresent()
        	        && existingMatch.get().getStatus() != StatusType.FINISHED
        	        && "FINISHED".equals(externalMatch.status());

        	Match match = existingMatch.orElseGet(Match::new);

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

            if (scoreAndFullTimeAreNotNull(externalMatch)) {

                match.setHomeScore(externalMatch.score().fullTime().home());

                match.setAwayScore(externalMatch.score().fullTime().away());
            } else {
                match.setHomeScore(null);
                match.setAwayScore(null);
            }
            matchRepository.save(match);
            
            if (newlyFinished) {
            	log.info("LIQUIDACION 1: Partido {} detectado como FINISHED. Marcador {}-{}",
                        match.getId(),
                        match.getHomeScore(),
                        match.getAwayScore());
            	try {
            		settlementServiceClient.notifyMatchFinished(match.getId(), toResponse(match));
            		log.info("LIQUIDACION 2: Settlement Service respondió correctamente para partido {}",
                            match.getId());
            	} catch(RestClientResponseException ex) {
                	throw ex;
                }
            }
        }
        return response.matches().size();
    }

	private boolean scoreAndFullTimeAreNotNull(FootballDataMatchResponse externalMatch) {
		return externalMatch.score() != null && externalMatch.score().fullTime() != null;
	}

	private boolean isResponseNull(FootballDataMatchesResponse response) {
		return response == null || response.matches() == null;
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
    
    private MatchResponse toResponse(Match match) {
		return new MatchResponse(
                match.getId(),
                match.getHomeTeam(),
                match.getAwayTeam(),
                match.getDateTime(),
                match.getCompetition(),
                match.getMatchDay(),
                match.getStatus(),
                match.getHomeScore(),
                match.getAwayScore()
        );
	}
}