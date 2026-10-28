package com.odds_service.service;

import com.odds_service.model.TeamRanking;
import com.odds_service.repository.TeamRankingRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OddsCalculatorService {

    private final TeamRankingRepo rankingRepository;
    private final double homeAdvantage;
    private final double margin;

    public OddsCalculatorService(TeamRankingRepo rankingRepository, @Value("${odds.model.home-advantage:60}")  double homeAdvantage, 
    		@Value("${odds.model.margin:0.06}") double margin) {
        this.rankingRepository = rankingRepository;
        this.homeAdvantage = homeAdvantage;
        this.margin = margin;
    }

    public Optional<CalculatedOdds> calculate(String homeTeam, String awayTeam) {

        Optional<TeamRanking> homeRanking = rankingRepository.findByTeamNameIgnoreCase(homeTeam);

        Optional<TeamRanking> awayRanking = rankingRepository.findByTeamNameIgnoreCase(awayTeam);

        if (rankingsAreNullOrEmpty(homeRanking, awayRanking)) {
            return Optional.empty();
        }

        double homeRating = homeRanking.get().getRatingPoints();

        double awayRating = awayRanking.get().getRatingPoints();

        double homeStrength = (homeRating - awayRating + homeAdvantage) / 400.0;

        double drawStrength = 0.0;

        double awayStrength = (awayRating - homeRating - homeAdvantage) / 400.0;

        double homeWeight = Math.exp(homeStrength);
        double drawWeight = Math.exp(drawStrength);
        double awayWeight = Math.exp(awayStrength);

        double totalWeight = homeWeight + drawWeight + awayWeight;

        double homeProbability = homeWeight / totalWeight;
        double drawProbability = drawWeight / totalWeight;
        double awayProbability = awayWeight / totalWeight;

        return Optional.of(new CalculatedOdds(toOdds(homeProbability), toOdds(drawProbability), toOdds(awayProbability)));
    }

	private boolean rankingsAreNullOrEmpty(Optional<TeamRanking> homeRanking, Optional<TeamRanking> awayRanking) {
		return homeRanking.isEmpty() || awayRanking.isEmpty();
	}

    private Double toOdds(double probability) {
    	return 1.0 / (probability * (1.0 + margin));
    }

    public record CalculatedOdds(
        Double homeWin,
        Double draw,
        Double awayWin
    ) {
    }
}