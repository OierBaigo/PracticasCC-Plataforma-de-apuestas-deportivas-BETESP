package com.odds_service.service;

import com.odds_service.client.MatchServiceClient;
import com.odds_service.dto.MatchResponse;
import com.odds_service.model.ResultType;
import com.odds_service.model.Odd;
import com.odds_service.repository.OddRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientResponseException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OddsUpdateService {

    private final MatchServiceClient matchServiceClient;
    private final OddsCalculatorService calculator;
    private final OddRepo oddRepository;

    public OddsUpdateService(MatchServiceClient matchServiceClient, OddsCalculatorService calculator, OddRepo oddRepository) {
        this.matchServiceClient = matchServiceClient;
        this.calculator = calculator;
        this.oddRepository = oddRepository;
    }

    public void updateAllOdds() {
    	List<MatchResponse> matches;
        try {
        	matches = matchServiceClient.getMatches();
        } catch(RestClientResponseException ex) {
        	throw ex;
        }

        for (MatchResponse match : matches) {
            if (match.id() == null) {
                continue;
            }
            if ("FINISHED".equalsIgnoreCase(match.status())) {
                deactivateOdds(match.id());
                continue;
            }
            if (!"SCHEDULED".equalsIgnoreCase(match.status()) && !"TIMED".equalsIgnoreCase(match.status())) {
                continue;
            }
            updateMatchOdds(match);
        }
    }

    @Transactional
    public void updateMatchOdds(MatchResponse match) {
        Optional<OddsCalculatorService.CalculatedOdds> result = calculator.calculate(match.homeTeam(), match.awayTeam());

        if (result.isEmpty()) {
            // Falta la puntuación de uno de los equipos.
            // No generamos cuotas inventadas.
            return;
        }

        OddsCalculatorService.CalculatedOdds calculated = result.get();

        saveOrUpdate(match.id(), ResultType.HOME_WIN, calculated.homeWin());

        saveOrUpdate(match.id(), ResultType.DRAW, calculated.draw());

        saveOrUpdate(match.id(), ResultType.AWAY_WIN, calculated.awayWin());
    }

    @Transactional
    public void deactivateOdds(Long matchId) {
        List<Odd> odds = oddRepository.findByMatchId(matchId);
        for (Odd odd : odds) {
            odd.deactivate();
        }
        oddRepository.saveAll(odds);
    }

    private void saveOrUpdate(Long matchId, ResultType resultType, Double value) {

        Optional<Odd> existing = oddRepository.findByMatchIdAndResultType(matchId, resultType);

        if (existing.isPresent()) {
            existing.get().setValue(value);
            oddRepository.save(existing.get());
        } else {
            oddRepository.save(new Odd(matchId, resultType, value));
        }
    }
}