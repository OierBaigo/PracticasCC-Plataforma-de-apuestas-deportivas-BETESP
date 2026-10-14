package com.match_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.match_service.dto.MatchResponse;
import com.match_service.dto.SyncMatchRequest;
import com.match_service.model.Match;
import com.match_service.service.MatchService;

@RestController
@RequestMapping(value = "matches")
public class MatchController {
	@Autowired
	private MatchService matchService;
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<MatchResponse> getMatches() {
		return matchService.getAllMatches()
	            .stream()
	            .map(match -> new MatchResponse(
	                    match.getId(),
	                    match.getHomeTeam(),
	                    match.getAwayTeam(),
	                    match.getDateTime(),
	                    match.getCompetition(),
	                    match.getMatchDay(),
	                    match.getStatus(),
	                    match.getHomeScore(),
	                    match.getAwayScore()
	            ))
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
	            .map(match -> new MatchResponse(
	                    match.getId(),
	                    match.getHomeTeam(),
	                    match.getAwayTeam(),
	                    match.getDateTime(),
	                    match.getCompetition(),
	                    match.getMatchDay(),
	                    match.getStatus(),
	                    match.getHomeScore(),
	                    match.getAwayScore()
	            ))
	            .toList();
	}
}
