package ch.atm.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import ch.atm.model.Account;

@Transactional
public interface AccountDAO extends JpaRepository<Account, Integer> {
    
}
