package com.odds_service.model;


import java.util.Objects;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "odds",
		uniqueConstraints = @UniqueConstraint(
        columnNames = {"matchId", "resultType"}
    ))
public class Odd {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Setter
	@Column(nullable = false)
	private Long matchId;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ResultType resultType;
	
	@Setter
	@Column(name = "odd_value", nullable = false)
	private Double value;
	
	@Setter
	@Column(nullable = false)
	private boolean active;
	
	public Odd(Long matchId, ResultType resultType, Double value) {
		this.matchId = matchId;
		this.resultType = resultType;
		this.value = value;
	}
	
	public void deactivate() {
	    this.active = false;
	}

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
		Odd other = (Odd) obj;
		return Objects.equals(id, other.id);
	}
}
