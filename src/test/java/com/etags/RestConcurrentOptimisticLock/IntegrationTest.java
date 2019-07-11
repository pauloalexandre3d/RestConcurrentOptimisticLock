package com.etags.RestConcurrentOptimisticLock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import com.optimistic.lock.domain.Account;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

	@Autowired
    private TestRestTemplate restTemplate;
	
	@Test(expected = ObjectOptimisticLockingFailureException.class)
	public void testShouldAssertStatesOfEntitys() {
		Account request1 = this.restTemplate.getForEntity("/accounts/1", Account.class).getBody();
		
		Account request2 = this.restTemplate.getForEntity("/accounts/1", Account.class).getBody();
		
		request2.setBalance(10L);
		this.restTemplate.exchange("/account/1", HttpMethod.PUT, new HttpEntity<>(request2), Account.class);
		
		this.restTemplate.exchange("/account/1", HttpMethod.PUT, new HttpEntity<>(request1), Account.class);
	}

}
