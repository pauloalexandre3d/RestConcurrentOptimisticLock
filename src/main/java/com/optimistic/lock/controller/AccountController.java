package com.optimistic.lock.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.optimistic.lock.domain.Account;
import com.optimistic.lock.exception.AccountNonExistentException;
import com.optimistic.lock.repositories.Accounts;

@RestController
@RequestMapping("/accounts")
public final class AccountController {

	@Autowired
    private Accounts accounts;

    @GetMapping(value = "/{id}")
    public Account getAccountById(@PathVariable("id") Long accountId) {
        Optional<Account> account = accounts.findById(accountId);

        if (!account.isPresent())
            throw new AccountNonExistentException(accountId);
        return account.get();
    }

    @PutMapping(value = "/")
    public void updateBalance(@RequestBody Account account) {
		try {
			accounts.save(account);
		} catch (ObjectOptimisticLockingFailureException e) {
            throw new AccountOldVersionedException(account.getId());
		}
    }
}
