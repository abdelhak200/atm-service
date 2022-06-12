package ch.atm.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ch.atm.model.AtmFormDto;
import ch.atm.service.AccountService;
import ch.atm.service.AtmService;

@WebMvcTest(AccountController.class)
@RunWith(MockitoJUnitRunner.class)
public class AccountControllerTest {
	
	@MockBean
    private AccountService accountService;

	@MockBean
    private AtmService atmService;
    
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;
    
    private AtmFormDto form;
    
    private  JSONObject data;
    
    @Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
    
    @BeforeEach
    public void setUp() throws JSONException {
    	
        data = new JSONObject();
        data.put("accountNumber", 123456789);
        data.put("pin", 1234);
        data.put("amount", 100.00);
        
        form = new AtmFormDto(123456789, 1234, 100);
    }

    @Test
    public void testGetBalance() throws Exception {
        when(accountService.verifyCredentials(form)).thenReturn(true);
        when(accountService.checkAvailableFunds(form)).thenReturn(1000.00);
        when(accountService.checkBalance(form)).thenReturn(800.00);
        when(atmService.maxFundsAvailable()).thenReturn(1500.00);
        
        mockMvc.perform(post("/api/v1/balance")
        		.contentType("application/json")
        		.content(data.toString()))
        .andDo(print())
        .andExpect(content().string(containsString("Balance: 800.0, Funds available: 1000.0")));

    }

    @Test
    public void testGetBalanceReturnErrorMessage() throws Exception {
        when(accountService.verifyCredentials(form)).thenReturn(false);

        mockMvc.perform(post("/api/v1/withdraw")
        		.contentType("application/json")
        		.content(data.toString()))
        .andDo(print())
        .andExpect(content().string(containsString("PIN is incorrect")));
        
    }

    @Test
    public void testWithdrawReturnNoteDispensed() throws Exception {
        when(accountService.verifyCredentials(form)).thenReturn(true);
        when(atmService.areFundsAvailable(anyDouble())).thenReturn(true);
        when(accountService.checkAvailableFunds(form)).thenReturn(1000.00);
        when(accountService.checkBalance(form)).thenReturn(800.00);
        when(accountService.withdraw(form)).thenReturn(true);
        when(atmService.maxFundsAvailable()).thenReturn(1500.00);

        HashMap<Double, Integer> notesHashMap = new HashMap<>();
        notesHashMap.put(50.0, 2);

        when(atmService.withdraw(anyDouble())).thenReturn(notesHashMap);

        mockMvc.perform(post("/api/v1/withdraw")
        		.contentType("application/json")
        		.content(data.toString()))
        .andDo(print())
        .andExpect(content().string(containsString("You are getting: 2x€50.0s")));
        
    }

    @Test
    public void testValidCredentialsAndWithdrawInvalidAmountReturnErrorMessage() throws Exception {
        when(accountService.verifyCredentials(form)).thenReturn(true);
        when(atmService.areFundsAvailable(anyInt())).thenReturn(false);

        mockMvc.perform(post("/api/v1/withdraw")
        		.contentType("application/json")
        		.content(data.toString()))
        .andDo(print())
        .andExpect(content().string(containsString("Sorry, insufficient funds in ATM.")));
        
    }

    @Test
    public void testWithdrawInvalidAmountFromAccountReturnErrorMessage() throws Exception {
        when(accountService.verifyCredentials(form)).thenReturn(true);
        when(atmService.areFundsAvailable(anyDouble())).thenReturn(true);
        when(accountService.checkAvailableFunds(form)).thenReturn(10.00);
        when(accountService.checkBalance(form)).thenReturn(5.00);
        when(accountService.withdraw(form)).thenReturn(false);
        when(atmService.maxFundsAvailable()).thenReturn(1500.00);

        String expectedString = "Insufficient funds in account.";
        mockMvc.perform(post("/api/v1/withdraw")
        		.contentType("application/json")
        		.content(data.toString()))
        .andDo(print())
        .andExpect(content().string(containsString(expectedString)));
        
    }

    @Test
    public void testWithdrawAndProblemWithAccountReturnErrorMessage() throws Exception {
        when(accountService.verifyCredentials(form)).thenReturn(true);
        when(atmService.areFundsAvailable(anyDouble())).thenReturn(true);
        when(accountService.checkAvailableFunds(form)).thenReturn(100.00);
        when(accountService.withdraw(form)).thenReturn(false);

        mockMvc.perform(post("/api/v1/withdraw")
        		.contentType("application/json")
        		.content(data.toString()))
        .andDo(print())
        .andExpect(content().string(containsString("Sorry, there was a problem with your withdrawal.")));
 
    }

    @Test
    public void testBadCredentialsAndWithdrawReturnErrorMessage() throws Exception {
        when(accountService.verifyCredentials(form)).thenReturn(false);

        mockMvc.perform(post("/api/v1/withdraw")
        		.contentType("application/json")
        		.content(data.toString()))
        .andDo(print())
        .andExpect(content().string(containsString("PIN is incorrect")));
        
    }
}
