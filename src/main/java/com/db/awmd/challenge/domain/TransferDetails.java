package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class TransferDetails {

	@NotNull
	@NotEmpty
	private final String accountFromId;

	@NotNull
	@NotEmpty
	private final String accountToId;

	@NotNull
	@Min(value = 1, message = "Amount to be trnsferred must be positive.")
	private BigDecimal transferAmount;

	@JsonCreator
	public TransferDetails( @JsonProperty("accountFromId") String accountFromId,
			@JsonProperty("transferAmount") BigDecimal transferAmount,
			@JsonProperty("accountToId") String accountToId) {
		this.accountFromId = accountFromId;
		this.transferAmount = transferAmount;
		this.accountToId = accountToId;
	}

}
