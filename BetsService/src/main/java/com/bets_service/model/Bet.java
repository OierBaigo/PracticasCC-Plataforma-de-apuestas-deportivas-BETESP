package com.bets_service.model;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "bets")
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long matchId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultType resultType;

    @Column(nullable = false)
    private Double stake;

    @Column(nullable = false)
    private Double oddsAtPlacement;

    @Column(nullable = false)
    private Double potentialReturn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BetStatus status = BetStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bet other = (Bet) obj;
		return Objects.equals(id, other.id);
	}
}
