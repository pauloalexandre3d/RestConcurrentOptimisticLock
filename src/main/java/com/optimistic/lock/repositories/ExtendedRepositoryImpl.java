package com.optimistic.lock.repositories;

import com.optimistic.lock.domain.Account;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.io.Serializable;
import java.util.Optional;

public class ExtendedRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
		implements ExtendedRepository<T, ID> {

	private EntityManager entityManager;

	public ExtendedRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityManager = entityManager;
	}

	@Transactional
	public Optional<Account> findById(Long id) {
		Account account = entityManager.find(Account.class, id, LockModeType.OPTIMISTIC_FORCE_INCREMENT	);
        return Optional.of(account);
	}

}