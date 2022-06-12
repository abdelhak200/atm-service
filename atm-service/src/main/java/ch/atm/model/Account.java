package ch.atm.model;


import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account {
	
    @Id
    private Integer accountNumber;
    private Integer pin;
    private Double balance;
    private Double overdraft;
    
    @Override
    public String toString() {
        return "Account [accountNumber=" + accountNumber + ", balance=" + balance + ", overdraft=" + overdraft
                + ", pin=" + pin + "]";
    }
}
