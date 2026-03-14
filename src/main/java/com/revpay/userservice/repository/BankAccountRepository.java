package com.revpay.userservice.repository;

import com.revpay.userservice.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    Optional<BankAccount> findByUserAndIsPrimaryTrue(User user);
}
