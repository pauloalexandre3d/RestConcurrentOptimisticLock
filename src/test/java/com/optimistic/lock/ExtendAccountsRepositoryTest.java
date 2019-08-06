package com.optimistic.lock;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.optimistic.lock.config.JpaConfig;
import com.optimistic.lock.domain.Account;
import com.optimistic.lock.repositories.Accounts;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { JpaConfig.class })
@WebAppConfiguration
public class ExtendAccountsRepositoryTest {

	@Autowired
	private Accounts accounts;

	@Before
	public void setup() {
		accounts.save(new Account(null, null, 10L));
		accounts.save(new Account(null, null, 20L));
		accounts.save(new Account(null, null, 30L));
	}

	@Test
	public void givenStudents_whenFindByName_thenOk() {
		List<Account> acounts = accounts.findAll();
		assertEquals("size incorrect", 3, acounts.size());
	}

}
