package com.optimistic.lock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.optimistic.lock.domain.Account;
import com.optimistic.lock.repositories.Accounts;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class IntegrationEtagsTest {

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
	public void testShouldAssertEtagExistsInHeader() throws Exception {
		Account accountSaved = (Account) accounts.save(new Account(null, null, 50L));
		ResponseEntity<Account> response = this.restTemplate
				.getForEntity("/accounts/" + accountSaved.getId() + "/custom-etag", Account.class);

		assertThat(response.getHeaders().getETag(), notNullValue());
	}

	@Test
	public void testShouldAssertRequestResourceWithSameEtagAndServerDoesNotRetrieveResource() throws Exception {
		Account accountSaved = (Account) accounts.save(new Account(null, null, 50L));
		ResponseEntity<Account> firstResponse = this.restTemplate
				.getForEntity("/accounts/" + accountSaved.getId() + "/custom-etag", Account.class);

		HttpHeaders headers = new HttpHeaders();
		headers.add("If-None-Match", firstResponse.getHeaders().getETag());

		HttpEntity<Account> entity = new HttpEntity<Account>(headers);
		ResponseEntity<Account> secondResponse = restTemplate
				.exchange("/accounts/" + accountSaved.getId() + "/custom-etag", HttpMethod.GET, entity, Account.class);

		assertThat(secondResponse.getStatusCodeValue(), equalTo(304));
		assertThat(secondResponse.getBody(), nullValue());
	}

	@Test
	public void testShouldAssertUpdateResourceChangedBetweenFirstAndSecondRequests() throws Exception {
		Account accountSaved = (Account) accounts.save(new Account(null, null, 50L));
		ResponseEntity<Account> firstResponse = this.restTemplate
				.getForEntity("/accounts/" + accountSaved.getId() + "/custom-etag", Account.class);

		String eTagValue = firstResponse.getHeaders().getETag();

		accountSaved.setBalance(10L);
		accounts.save(accountSaved);

		HttpHeaders headers = new HttpHeaders();
		headers.add("If-None-Match", eTagValue);
		HttpEntity<Account> entity = new HttpEntity<Account>(headers);
		ResponseEntity<Account> secondResponse = restTemplate
				.exchange("/accounts/" + accountSaved.getId() + "/custom-etag", HttpMethod.GET, entity, Account.class);

		assertThat(secondResponse.getStatusCodeValue(), equalTo(200));
		assertThat(secondResponse.getBody().getBalance(), equalTo(10L));
	}
	
	@Test
	public void testName() throws Exception {
		Account accountSaved = (Account) accounts.save(new Account(null, null, 50L));
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("If-Match", "pumpkinOrAnyThingElse");
		HttpEntity<Account> entity = new HttpEntity<Account>(headers);
		ResponseEntity<Account> secondResponse = restTemplate
				.exchange("/accounts/" + accountSaved.getId() + "/custom-etag", HttpMethod.GET, entity, Account.class);
		
		assertThat(secondResponse.getStatusCodeValue(), equalTo(200));
	}

}
