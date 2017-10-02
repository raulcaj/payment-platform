package br.com.raulcaj.accountmodule;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.raulcaj.accountmodule.domain.Account;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class AccountModuleApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;
	
	@LocalServerPort
	private long port;
	
	private String getUri(String path) {
		return String.format("http://localhost:%d%s", port, path);
	}
	
	@Test
	public void limitGetAll() {
		final ResponseEntity<List<Account>> response = restTemplate.exchange(getUri("/v1/accounts/limits"), HttpMethod.GET, null, new ParameterizedTypeReference<List<Account>>() {});
		assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
		assertTrue(CollectionUtils.isNotEmpty(response.getBody()));
		assertTrue(response.getBody().stream().map(Account::getId).allMatch(Objects::nonNull));
	}
	
	@Test
	public void limitGetOne() {
		final ResponseEntity<Account> response = restTemplate.exchange(getUri("/v1/accounts/{id}"), HttpMethod.GET, null, Account.class, 1L);
		assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
		assertTrue(response.getBody().getId() == 1L);
		assertTrue(response.getBody().getAvailableCreditLimit().compareTo(new BigDecimal(5000L)) == 0);
	}
	
	@Test
	public void updateOneLimit() {
		final Account request = Account.createRequest(-1000L, 0L);
		final ResponseEntity<Void> responsePatch = restTemplate.exchange(getUri("/v1/accounts/{id}"), HttpMethod.PATCH, new HttpEntity<>(request), Void.class, 10L);
		assertTrue(HttpStatus.OK.equals(responsePatch.getStatusCode()));
		final ResponseEntity<Account> response = restTemplate.exchange(getUri("/v1/accounts/{id}"), HttpMethod.GET, null, Account.class, 10L);
		assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
		assertTrue(response.getBody().getId() == 10L);
		assertTrue(response.getBody().getAvailableCreditLimit().compareTo(new BigDecimal(4000L)) == 0);
	}

}
