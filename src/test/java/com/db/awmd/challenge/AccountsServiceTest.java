package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransferDetails;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InSufficentBalanceException;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

	@Autowired
	private AccountsService accountsService;
	
	Account accountTo;
	
	@Before
	public void accounts() throws Exception {
		this.accountTo = new Account("T-99");
	}

	@Test
	public void addAccount() throws Exception {
		Account account = new Account("Id-123");
		account.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(account);

		assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
	}

	@Test
	public void addAccount_failsOnDuplicateId() throws Exception {
		String uniqueId = "Id-" + System.currentTimeMillis();
		Account account = new Account(uniqueId);
		this.accountsService.createAccount(account);

		try {
			this.accountsService.createAccount(account);
			fail("Should have failed when adding duplicate account");
		} catch (DuplicateAccountIdException ex) {
			assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
		}

	}

	@Test
	public void transferAmount_passPositive() throws Exception{
		TransferDetails transferDetails = new TransferDetails("I-88",BigDecimal.valueOf(7000),"S-99");
		assertThat(transferDetails.getTransferAmount().compareTo(BigDecimal.valueOf(0)) > 0);
	}
	
	@Test
	public void transferAmount_failPositive() throws Exception {
		TransferDetails transferDetails = new TransferDetails("I-88",BigDecimal.valueOf(-700),"S-99");
		assertFalse(transferDetails.getTransferAmount().compareTo(BigDecimal.valueOf(0)) > 0);
	}
	
	@Test(expected=InSufficentBalanceException.class)
	public void transferAmount_InsufficientBalance() throws InSufficentBalanceException {
		TransferDetails transferDetails = new TransferDetails("F-88",BigDecimal.valueOf(700),"T-99");
		
		Account accountFrom = new Account(transferDetails.getAccountFromId());
		accountFrom.setBalance(BigDecimal.valueOf(500));
		
		Mockito.when(accountsService.getAccount(transferDetails.getAccountFromId())).thenReturn(accountFrom);
		
		Mockito.when(accountsService.getAccount(transferDetails.getAccountToId())).thenReturn(this.accountTo);
		
		accountsService.transferAmount(transferDetails);
		
	}
}
