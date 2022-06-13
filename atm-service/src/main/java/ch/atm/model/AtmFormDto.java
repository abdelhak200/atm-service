package ch.atm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AtmFormDto {
	
    private int accountNumber;
    private int pin;
    //this field can be used in withdraw/deposit in case of get balance it is not necessary 
    private double amount;
    
}
