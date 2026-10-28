package com.odds_service.controller;

import com.odds_service.dto.OddResponse;
import com.odds_service.model.Odd;
import com.odds_service.repository.OddRepo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/internal/odds")
public class InternalOddsController {

	private final OddRepo oddRepository;

    InternalOddsController(OddRepo oddRepository) {
        this.oddRepository = oddRepository;
    }

    @GetMapping("/match/{matchId}")
    public List<OddResponse> getOddsByMatch(@PathVariable Long matchId) {

        if (matchId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El identificador del partido debe ser positivo");
        }

        return oddRepository.findByMatchIdAndActiveTrue(matchId)
        		.stream()
        		.map(odd -> toResponse(odd))
        		.toList();
    }

	private OddResponse toResponse(Odd odd) {
		return new OddResponse(
				odd.getId(),
				odd.getMatchId(),
				odd.getResultType(),
				odd.getValue(),
				true);
	}
}