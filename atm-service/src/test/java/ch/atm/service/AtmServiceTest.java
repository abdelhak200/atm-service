package ch.atm.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AtmServiceTest {

    private AtmService atmService;

    private HashMap<Double, Integer> initialNotes = new HashMap<>();

    @BeforeEach
    public void setUp() {
        initialNotes.put(50.00, 10);
        initialNotes.put(20.00, 30);
        initialNotes.put(10.00, 30);
        initialNotes.put(5.00, 20);

        atmService = new AtmService();
    }

    @AfterEach
    public void tearDown() {
        atmService = null;
        initialNotes = null;
    }

    @Test
    public void givenFundsAreAvailableReturnTrue() {
        assertTrue(atmService.areFundsAvailable(10.00));
    }

    @Test
    public void givenFundsAreNotAvailableReturnFalse() {
        assertFalse(atmService.areFundsAvailable(20000.00));
    }

    @Test
    public void returnFundsAvailable() {
        assertEquals(1500.00, atmService.maxFundsAvailable());
    }

    @Test
    public void givenWithdraw50ReturnCorrectHashMap() {
        HashMap<Double, Integer> expectedNotes = new HashMap<>();
        expectedNotes.put(50.00, 1);
        expectedNotes.put(20.00, 0);
        expectedNotes.put(10.00, 0);
        expectedNotes.put(5.00, 0);

        assertEquals(expectedNotes, atmService.withdraw(50.00));
    }

    @Test
    public void givenWithdraw25ReturnCorrectHashMap() {
        HashMap<Double, Integer> expectedNotes = new HashMap<>();
        expectedNotes.put(50.00, 0);
        expectedNotes.put(20.00, 1);
        expectedNotes.put(10.00, 0);
        expectedNotes.put(5.00, 1);

        assertEquals(expectedNotes, atmService.withdraw(25.00));
    }

    @Test
    public void givenWithdrawInvalidAmountThrowRuntimeException() {
        Exception thrown = assertThrows(
            RuntimeException.class, 
            () -> atmService.withdraw(187.00));

        assertEquals("Invalid Amount. Not enough notes to make up your request.", thrown.getMessage());
    }

    @Test
    public void givenWithdraw2000ThrowRuntimeException() {

        Exception thrown = assertThrows(
            RuntimeException.class, 
            () -> atmService.withdraw(2000.00));

        assertEquals("Sorry, not enough cash in the ATM. Please try again.", thrown.getMessage());
    }
    
}
