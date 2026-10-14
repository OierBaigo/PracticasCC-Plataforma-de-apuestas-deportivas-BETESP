package com.match_service.dto;

public record FootballDataMatchResponse(
    Long id,
    String utcDate,
    String status,
    Integer matchday,
    FootballDataTeamDto homeTeam,
    FootballDataTeamDto awayTeam,
    FootballDataCompetitionDto competition,
    FootballDataScoreDto score
) {
    public record FootballDataTeamDto(
        Long id,
        String name
    ) {}

    public record FootballDataCompetitionDto(
        Long id,
        String name,
        String code
    ) {}

    public record FootballDataScoreDto(
        FootballDataFullTimeDto fullTime
    ) {}

    public record FootballDataFullTimeDto(
        Integer home,
        Integer away
    ) {}
}