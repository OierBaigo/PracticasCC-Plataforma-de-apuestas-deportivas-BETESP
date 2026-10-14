package com.match_service.dto;

import java.util.List;

public record FootballDataMatchesResponse(
    List<FootballDataMatchDto> matches
) {}