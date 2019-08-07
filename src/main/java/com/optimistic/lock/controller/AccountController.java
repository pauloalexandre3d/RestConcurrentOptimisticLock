package com.optimistic.lock.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.optimistic.lock.controller.exception.AccountNonExistentException;
import com.optimistic.lock.controller.exception.AccountOldVersionedException;
import com.optimistic.lock.domain.Account;
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

	@GetMapping(value = "/{id}/custom-etag")
	public ResponseEntity<Account> getAccountByIdEtag(@PathVariable("id") Long accountId) {
		// @formatter:off

    	Optional<Account> account = accounts.findById(accountId);

        if (!account.isPresent())
            throw new AccountNonExistentException(accountId);
        return ResponseEntity
        		.ok()
        		.eTag(Long.toString(
        				account.get().getVersion()))
        		.body(account.get());
        
     // @formatter:on
	}

	@PutMapping(value = "/custom-etag")
	public ResponseEntity<Account> updateBalanceWithEtag(@RequestHeader Map<String, String> headers,
			@RequestBody Account account) {
		Account accountUpdated;
		try {
			if (headers.containsKey("if-match") && !headers.get("if-match").trim()
					.equals(accounts.findById(account.getId()).get().getVersion().toString())) {
				return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
			}

			accountUpdated = accounts.save(account);
		} catch (ObjectOptimisticLockingFailureException e) {
			throw new AccountOldVersionedException(account.getId());
		}
		return new ResponseEntity<Account>(accountUpdated, HttpStatus.OK);
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
