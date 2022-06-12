package ch.atm.controller;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.atm.model.AtmFormDto;
import ch.atm.service.AccountService;
import ch.atm.service.AtmService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class AccountController {

	@Autowired
	private AccountService accountService;

	@Autowired
	private AtmService atmService;

	@PostMapping(value = "/balance")
	public String getBalance(@Validated @RequestBody AtmFormDto form) throws AccountNotFoundException {

		log.info("Getting the balance {} ", accountService.checkBalance(form));

		if (accountService.verifyCredentials(form)) {
			double maxAvailable = (accountService.checkAvailableFunds(form) > atmService.maxFundsAvailable())
					? atmService.maxFundsAvailable()
					: accountService.checkAvailableFunds(form);
			return "Balance: " + accountService.checkBalance(form) + ", Funds available: " + maxAvailable;
		}
		return "PIN is incorrect";
	}

	@PostMapping(value = "/withdraw")
	public String withdraw(@Validated @RequestBody AtmFormDto form) throws AccountNotFoundException {
		if (accountService.verifyCredentials(form)) {
			if (!atmService.areFundsAvailable(form.getAmount())) {
				return "Sorry, insufficient funds in ATM.";
			} else if (form.getAmount() > accountService.checkAvailableFunds(form)) {
				return "Insufficient funds in account.";
			} else {
				if (accountService.withdraw(form)) {
					HashMap<Double, Integer> dispensedNotes = atmService.withdraw(form.getAmount());
					StringBuilder returnString = new StringBuilder();
					returnString.append("You are getting: ");
					for (Map.Entry<Double, Integer> note : dispensedNotes.entrySet()) {
						if (note.getValue() > 0) {
							returnString.append(note.getValue() + "x€" + note.getKey() + "s ");
						}
					}
					returnString.append("\nRemaining balance: " + accountService.checkBalance(form));
					return returnString.toString();
				} else {
					log.info("Sorry, there was a problem with your withdrawal.");

					return "Sorry, there was a problem with your withdrawal.";
				}
			}
		} else {
			return "PIN is incorrect";
		}

	}
}
