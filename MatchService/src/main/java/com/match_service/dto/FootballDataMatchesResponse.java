package com.match_service.dto;

import java.util.List;

public record FootballDataMatchesResponse(
    List<FootballDataMatchResponse> matches
) {}