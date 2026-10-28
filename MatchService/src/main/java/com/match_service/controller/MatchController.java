package com.match_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.match_service.client.SettlementServiceClient;
import com.match_service.dto.MatchResponse;
import com.match_service.dto.SyncMatchRequest;
import com.match_service.dto.SyncStatusResponse;
import com.match_service.model.Match;
import com.match_service.model.MatchSyncStatus;
import com.match_service.model.StatusType;
import com.match_service.repository.MatchRepo;
import com.match_service.service.MatchService;

@RestController
@RequestMapping(value = "matches")
public class MatchController {
	
	private final MatchService matchService;
	private final MatchRepo matchRepo;
	private final SettlementServiceClient settlementServiceClient;
    private final MatchSyncStatus matchSyncStatus;


	MatchController(MatchService matchService, MatchRepo matchRepo, SettlementServiceClient settlementServiceClient,
			MatchSyncStatus matchSyncStatus) {
		this.matchService = matchService;
		this.matchRepo = matchRepo;
		this.settlementServiceClient = settlementServiceClient;
		this.matchSyncStatus = matchSyncStatus;
	}
	
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<MatchResponse> getMatches() {
		return matchService.getAllMatches()
	            .stream()
	            .map(match -> toResponse(match))
	            .toList();
	}
	
	@GetMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public MatchResponse getMatchById(@PathVariable Long id) {
		Match match = matchService.getMatchById(id);
		return new MatchResponse(match.getId(), match.getHomeTeam(), match.getAwayTeam(), match.getDateTime(),
				match.getCompetition(), match.getMatchDay(), match.getStatus(), match.getHomeScore(), match.getAwayScore());
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Match createMatch(@RequestBody Match match) {
	    return matchService.saveMatch(match);
	}
	
	@PostMapping(value = "/internal/sync")
	@ResponseStatus(HttpStatus.CREATED)
	public int syncMatches(@RequestBody SyncMatchRequest request) {
	    return matchService.syncLaLigaMatches(request.season());
	}
	
	@GetMapping("/competitions/{competition}/matches")
	@ResponseStatus(HttpStatus.OK)
	public List<MatchResponse> getMatchesByCompetition(@PathVariable String competition) {
		return matchService.getMatchesByCompetition(competition)
	            .stream()
	            .map(match -> toResponse(match))
	            .toList();
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
	
	@PostMapping("/internal/test-settlement/{matchId}")
	public ResponseEntity<String> testSettlement(@PathVariable Long matchId,@RequestParam Integer homeScore,
	        @RequestParam Integer awayScore) {

	    Match match = matchRepo.findById(matchId)
	            .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado"));

	    MatchResponse response = new MatchResponse(
                match.getId(),
                match.getHomeTeam(),
                match.getAwayTeam(),
                match.getDateTime(),
                match.getCompetition(),
                match.getMatchDay(),
                StatusType.FINISHED,
                homeScore,
                awayScore
        );

	    settlementServiceClient.notifyMatchFinished(matchId, response);

	    return ResponseEntity.ok("Notificación de prueba enviada a Settlement Service");
	}
	
	@GetMapping("/internal/sync-status")
    public SyncStatusResponse getSyncStatus() {
        return new SyncStatusResponse(matchSyncStatus.isInitialSyncCompleted());
    }
}
