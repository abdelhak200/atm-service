package ch.atm.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.security.auth.login.AccountNotFoundException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ch.atm.dao.AccountDAO;
import ch.atm.model.Account;
import ch.atm.model.AtmFormDto;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountDAO accountDAO;

    private AccountService accountService;

    Account account1 = new Account(1234,1234,100.00,100.00);
    Account account2 = new Account(4321,9876,1500.00,200.00);

    List<Account> accountList;

    @BeforeEach
    public void setUp() {
        accountService = new AccountService(accountDAO);

        accountList = new ArrayList<Account>();
        accountList.add(account1);
        accountList.add(account2);
    }

    @AfterEach
    public void tearDown() {
        accountService = null;
        accountList = null;
        account1 = account2 = null;
    }

    @Test
    public void givenValidCredentialsShouldReturnTrue() throws AccountNotFoundException {
    	AtmFormDto form = new AtmFormDto(1234, 1234, 0);
    	
        when(accountDAO.findById(1234)).thenReturn(Optional.of(account1));
        Boolean isValidCredentials = accountService.verifyCredentials(form);
        assertTrue(isValidCredentials);
    }

    @Test
    public void givenInvalidCredentialsShouldReturnFalse() throws AccountNotFoundException {
    	AtmFormDto form = new AtmFormDto(1234, 0000, 0);
    	
        when(accountDAO.findById(1234)).thenReturn(Optional.of(account1));
        Boolean isValidCredentials = accountService.verifyCredentials(form);
        assertFalse(isValidCredentials);
    }

    @Test
    public void givenValidCredentialsAndCheckBalanceShouldReturnBalance() throws AccountNotFoundException {
    	AtmFormDto form = new AtmFormDto(1234, 1234, 100.00);
    	
        when(accountDAO.findById(anyInt())).thenReturn(Optional.of(account1));
        double returnedBalance = accountService.checkBalance(form);
        assertEquals(100.00, returnedBalance);
    }

    @Test
    public void givenInvalidCredentialsAndCheckBalanceShouldThrowRuntimeException() {
    	AtmFormDto form = new AtmFormDto(1234, 1234, 0);
    	
        when(accountDAO.findById(1234)).thenReturn(Optional.of(account2));

        Exception thrown = assertThrows(
            RuntimeException.class, 
            () -> accountService.checkBalance(form));

        assertEquals("Balance check failed.", thrown.getMessage());
    }

    @Test
    public void givenValidCredentialsAndCheckAvailableFunds_shouldReturnAvailableFunds() throws AccountNotFoundException {
    	AtmFormDto form = new AtmFormDto(1234, 1234, 0);
    	
        when(accountDAO.findById(anyInt())).thenReturn(Optional.of(account1));
        double returnedBalance = accountService.checkAvailableFunds(form);
        assertEquals(200.00, returnedBalance);
    }

    @Test
    public void givenInvalidCredentialsAndCheckAvailableFundsShouldThrowRuntimeException() {
    	AtmFormDto form = new AtmFormDto(1234, 1234, 0);
    	
        when(accountDAO.findById(1234)).thenReturn(Optional.of(account2));

        Exception thrown = assertThrows(
            RuntimeException.class, 
            () -> accountService.checkAvailableFunds(form));

        assertEquals("Available funds check failed.", thrown.getMessage());
    }

    @Test
    public void givenValidCredentialsAndWithdrawValidAmountShouldReturnTrue() throws AccountNotFoundException {
    	AtmFormDto form = new AtmFormDto(1234, 1234, 10.00);

        when(accountDAO.findById(anyInt())).thenReturn(Optional.of(account1));
        when(accountDAO.save(any(Account.class))).then(returnsFirstArg());

        Boolean answer = accountService.withdraw(form);

        assertTrue(answer);
    }

    @Test
    public void givenValidCredentialsAndWithdrawValidAmountUsingOverdraftShouldReturnTrue() throws AccountNotFoundException {
    	AtmFormDto form = new AtmFormDto(1234, 1234, 150.00);
    	
        when(accountDAO.findById(anyInt())).thenReturn(Optional.of(account1));
        when(accountDAO.save(any(Account.class))).then(returnsFirstArg());

        Boolean answer = accountService.withdraw(form);

        assertTrue(answer);
    }

    @Test
    public void givenValidCredentialsAndWithdrawInvalidAmountUsingOverdraftShouldReturnFalse() throws AccountNotFoundException {
    	AtmFormDto form = new AtmFormDto(1234, 1234, 500.00);
    	
        when(accountDAO.findById(anyInt())).thenReturn(Optional.of(account1));

        Boolean answer = accountService.withdraw(form);

        assertFalse(answer);
    }

    @Test
    public void givenInvalidCredentialsAndWithdrawShouldReturnFalse() throws AccountNotFoundException {
    	AtmFormDto form = new AtmFormDto(1234, 0000, 100.00);
    	
        when(accountDAO.findById(anyInt())).thenReturn(Optional.of(account1));

        Boolean answer = accountService.withdraw(form);

        assertFalse(answer);
    }

    @Test
    public void givenInvalidAccountNumberShouldThrowNotFoundError() {
    	AtmFormDto form = new AtmFormDto(1234, 1234, 0);
    	
        when(accountDAO.findById(anyInt())).thenReturn(Optional.empty());

        Exception thrown = assertThrows(
            AccountNotFoundException.class, 
            () -> accountService.verifyCredentials(form));

        assertEquals("Cannot find account number: 1234", thrown.getMessage());
    }
    

    
}
