package com.optimistic.lock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimistic.lock.domain.Account;
import com.optimistic.lock.repositories.Accounts;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@RunWith(SpringRunner.class)
@WebMvcTest
public class AccountControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private Accounts accounts;

	private JacksonTester<Account> jsonAccount;

	@Before
	public void setup() {
		JacksonTester.initFields(this, new ObjectMapper());
	}

	@Test
	public void testShouldPerformGetAccountById() throws Exception {
//		@formatter:off

//		given
        given(accounts.findById(1L))
                .willReturn(Optional.of(new Account(1L, 1L, 50L)));

//		when		
        MockHttpServletResponse response = mockMvc.perform(
                get("/accounts/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

//		then
        assertThat(response.getStatus(), equalTo(HttpStatus.OK.value()));

//        @formatter:on
	}

	@Test
	public void testShouldPerformUpdateBalance() throws Exception {
//        @formatter:off

//        given
        given(accounts.save(new Account(1L, 1L, 10L)))
                .willReturn(new Account(1L, 1L, 10L));

//        when
        MockHttpServletResponse response = mockMvc.perform(
                put("/accounts/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAccount.write(new Account(1L, 1L, 10L)).getJson()))
                .andReturn()
                .getResponse();

        //		then
        assertThat(response.getStatus(), equalTo(HttpStatus.OK.value()));
//        @formatter:on
	}

}
