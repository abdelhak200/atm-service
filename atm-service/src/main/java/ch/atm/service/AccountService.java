package ch.atm.service;


import java.util.Optional;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.atm.dao.AccountDAO;
import ch.atm.model.Account;
import ch.atm.model.AtmFormDto;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountService {

	@Autowired
    private AccountDAO accountDAO; 

    public boolean verifyCredentials(AtmFormDto form) throws AccountNotFoundException {
        Optional<Account> account = accountDAO.findById(form.getAccountNumber());
        if (account.isEmpty()) {
            throw new AccountNotFoundException("Cannot find account number: " + form.getAccountNumber());
        }
        if ( account.get().getPin() == form.getPin()) {
        	return true;
        }
        return false;
    }

    public double checkBalance(AtmFormDto form) throws AccountNotFoundException {

        if (verifyCredentials(form) && accountDAO.findById(form.getAccountNumber()).get().getPin() == form.getPin()) {
                return accountDAO.findById(form.getAccountNumber()).get().getBalance();
        } else {
            throw new RuntimeException("Balance check failed.");
        }
    }

    public double checkAvailableFunds(AtmFormDto form) throws AccountNotFoundException {
        if (verifyCredentials(form)) {
            return accountDAO.findById(form.getAccountNumber()).get().getBalance() + accountDAO.findById(form.getAccountNumber()).get().getOverdraft();
        } else {
            throw new RuntimeException("Available funds check failed.");
        }

    }

    public boolean withdraw(AtmFormDto form) throws AccountNotFoundException {
        if (verifyCredentials(form)) {
            if (form.getAmount() <= (accountDAO.findById(form.getAccountNumber()).get().getBalance() + accountDAO.findById(form.getAccountNumber()).get().getOverdraft())) {
                Optional<Account> optionalAccount = accountDAO.findById(form.getAccountNumber());
                Account updatedAccount = optionalAccount.get();

                //Check if we need to use overdraft or not.
                if (form.getAmount() <= updatedAccount.getBalance()) {
                    updatedAccount.setBalance(updatedAccount.getBalance() - form.getAmount());
                    accountDAO.save(updatedAccount);
                    return true;
                } else if (form.getAmount() <= (updatedAccount.getBalance() + updatedAccount.getOverdraft())) {
                    double diff = form.getAmount() - updatedAccount.getBalance();
                    updatedAccount.setBalance(Double.valueOf(0));
                    updatedAccount.setOverdraft(updatedAccount.getOverdraft() - diff);
                    accountDAO.save(updatedAccount);
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }
    
}
