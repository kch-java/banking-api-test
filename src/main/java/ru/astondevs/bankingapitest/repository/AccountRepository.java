package ru.astondevs.bankingapitest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.astondevs.bankingapitest.model.Account;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByBeneficiaryName(String beneficiaryName);
}
