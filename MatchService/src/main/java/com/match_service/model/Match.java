package com.match_service.model;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "matches")
public class Match {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String homeTeam;
	
	@Column(nullable = false)
	private String awayTeam;
	
	@Column(nullable = false)
	private LocalDateTime dateTime;
	
	@Column(nullable = false)
	private String competition = "LaLiga";
	
	@Column(nullable = false)
	private Integer matchDay;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusType status;
	
	@Column
	private Integer homeScore;
	
	@Column
	private Integer awayScore;
	
	@Column(unique = true)
	private Long externalId;

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
		Match other = (Match) obj;
		return Objects.equals(id, other.id);
	}
}
