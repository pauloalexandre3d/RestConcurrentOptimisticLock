package com.optimistic.lock;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat; 

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.optimistic.lock.domain.Account;
import com.optimistic.lock.repositories.Accounts;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private Accounts accounts;

	@LocalServerPort
	private String port;

	@Before
	public void setup() {
		accounts.deleteAll();
	}

	@Test
	public void testShouldAssertStatesOfEntitysIsOutOfDate() {
		Account accountSaved = (Account) accounts.save(new Account(null, 1L, 50L));

		ResponseEntity<Account> responseEntity1 = this.restTemplate.getForEntity("/accounts/" + accountSaved.getId(),
				Account.class);
		Account request1 = responseEntity1.getBody();

		Account request2 = responseEntity1.getBody();

		request2.setBalance(10L);
		this.restTemplate.exchange("/accounts/", HttpMethod.PUT, new HttpEntity<>(request2), Account.class);

		ResponseEntity<Account> responseEntity = this.restTemplate.exchange("/accounts/", HttpMethod.PUT,
				new HttpEntity<>(request1), Account.class);
		assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.PRECONDITION_FAILED));
	}

	@Test
	public void testShouldAssertGetEntity() {
		Account accountSaved = (Account) accounts.save(new Account(null, null, 50L));
		ResponseEntity<Account> response = this.restTemplate.getForEntity("/accounts/" + accountSaved.getId(),
				Account.class);

		Account account = response.getBody();

		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(account.getBalance(), equalTo(50L));
	}

	@Test
	public void testShouldAssertUpdateEntity() {
		Account accountSaved = (Account) accounts.save(new Account(null, 1L, 50L));

		accountSaved.setBalance(10L);

		ResponseEntity<Account> responseEntity = this.restTemplate.exchange("/accounts/", HttpMethod.PUT, new HttpEntity<>(accountSaved), Account.class);

		Account accountUpdated = (Account) accounts.findById(accountSaved.getId()).get();

		assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(accountUpdated.getBalance(), equalTo(10L));
	}
	
	@Test
	public void testShouldAssertEtagExistsInHeader() throws Exception {
		Account accountSaved = (Account) accounts.save(new Account(null, null, 50L));
		ResponseEntity<Account> response = this.restTemplate.getForEntity("/accounts/" + accountSaved.getId()+"/custom-etag",
				Account.class);

		assertThat(response.getHeaders().getETag(), notNullValue());
	}
	
	@Test
	public void testShouldAssertEtagExistsInHeader2() throws Exception {
		Account accountSaved = (Account) accounts.save(new Account(null, null, 50L));
		ResponseEntity<Account> response = this.restTemplate.getForEntity("/accounts/" + accountSaved.getId()+"/custom-etag",
				Account.class);

		assertThat(response.getHeaders().getETag(), notNullValue());
		
		this.restTemplate.getForEntity("/accounts/" + accountSaved.getId()+"/custom-etag",
				Account.class).getHeaders().add("If-None-Match", response.getHeaders().getETag());
	}
}
