package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransferDetails;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InSufficentBalanceException;
import com.db.awmd.challenge.service.NotificationService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {
	
	NotificationService notificationService; 
	
	private final Map<String, Account> accounts = new ConcurrentHashMap<>();

	@Override
	public void createAccount(Account account) throws DuplicateAccountIdException {
		Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
		if (previousAccount != null) {
			throw new DuplicateAccountIdException(
					"Account id " + account.getAccountId() + " already exists!");
		}
	}

	@Override
	public Account getAccount(String accountId) {
		return accounts.get(accountId);
	}

	@Override
	public void clearAccounts() {
		accounts.clear();
	}

	@Override
	public synchronized void transferAmount(TransferDetails transerDetails) throws InSufficentBalanceException {
		
		Account accountFrom = getAccount(transerDetails.getAccountFromId());
        Account accountTo = getAccount(transerDetails.getAccountToId());
        
        if(accountFrom.getBalance().compareTo(transerDetails.getTransferAmount()) >= 0) {
        	accountFrom.setBalance(accountFrom.getBalance().subtract(transerDetails.getTransferAmount()));
        	accountTo.setBalance(accountTo.getBalance().add(transerDetails.getTransferAmount()));
        	
        	accounts.put(accountFrom.getAccountId(), accountFrom);
        	accounts.put(accountTo.getAccountId(), accountTo);
        	
        	notificationService.notifyAboutTransfer(accountFrom, transerDetails.getTransferAmount().toString()+"Transferred to" + accountTo.getAccountId());
        	notificationService.notifyAboutTransfer(accountTo, transerDetails.getTransferAmount().toString()+"Receieved from" + accountFrom.getAccountId());
        	
        }
        else {
        	throw new InSufficentBalanceException("InSufficient balance in " + transerDetails.getAccountFromId());
        }
		
	}

}
