package br.com.raulcaj.transactionmodule;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.raulcaj.transactionmodule.TransactionModuleApplicationTests.TestConfig;
import br.com.raulcaj.transactionmodule.controller.NotAcceptableException;
import br.com.raulcaj.transactionmodule.domain.AccountService;
import br.com.raulcaj.transactionmodule.domain.TransactionRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes=TestConfig.class)
@ActiveProfiles("test")
public class TransactionModuleApplicationTests {

	@Configuration
	@Import(TransactionModuleApplication.class)
	@Profile("test")
	public static class TestConfig {
		private static final Map<Long, Pair<BigDecimal, BigDecimal>> REPOSITORY = new HashMap<>();
		static {
			REPOSITORY.put(1L, Pair.of(new BigDecimal(5000L), new BigDecimal(5000L)));
		}
		@Bean
		public AccountService accountService() {
			return new AccountService() {
				@Override
				public void executeAccountLimitUpdate(Long accountId, Pair<BigDecimal, BigDecimal> amount)
						throws NotAcceptableException {
					if (!REPOSITORY.containsKey(accountId)) {
						throw new NotAcceptableException();
					}
					final Pair<BigDecimal, BigDecimal> account = REPOSITORY.get(accountId);
					final Pair<BigDecimal, BigDecimal> updatedAccount = Pair.of(account.getFirst().add(amount.getFirst()),
							account.getSecond().add(amount.getSecond()));
					if(BigDecimal.ZERO.compareTo(updatedAccount.getFirst()) > 0 ||
							BigDecimal.ZERO.compareTo(updatedAccount.getSecond()) > 0) {
						throw new NotAcceptableException();
					}
					REPOSITORY.put(accountId, updatedAccount);
				}

				@Override
				public boolean accountNotExist(Long accountId) throws NotAcceptableException {
					return !REPOSITORY.containsKey(accountId);
				}
			};
		}
	}

	@LocalServerPort
	private long localPort;

	@Autowired
	private TestRestTemplate restTemplate;
	
	private String getUri(String path) {
		return String.format("http://localhost:%d%s", localPort, path);
	}

	@Test
	public void createCreditTx() {
		final TransactionRequest tr = TransactionRequest.createTxRequest(1L, 1L, new BigDecimal(100L));
		final ResponseEntity<Void> response = sendRequest(tr);
		assertTrue(HttpStatus.OK.equals(response.getStatusCode()));
	}
	
	@Test
	public void createOverlimitedTx() {
		final TransactionRequest tr = TransactionRequest.createTxRequest(1L, 1L, new BigDecimal(10000L));
		final ResponseEntity<Void> response = sendRequest(tr);
		assertTrue(HttpStatus.NOT_ACCEPTABLE.equals(response.getStatusCode()));
	}
	
	@Test
	public void createTxAndPayIt() {
		final TransactionRequest tr = TransactionRequest.createTxRequest(1L, 1L, new BigDecimal(1000L));
		final ResponseEntity<Void> responseTx = sendRequest(tr);
		assertTrue(HttpStatus.OK.equals(responseTx.getStatusCode()));
		final TransactionRequest py = TransactionRequest.createTxRequest(1L, 4L, new BigDecimal(1000L));
		final ResponseEntity<Void> responsePy = sendPayment(py);
		assertTrue(HttpStatus.OK.equals(responsePy.getStatusCode()));
		final TransactionRequest bigTx = TransactionRequest.createTxRequest(1L, 1L, new BigDecimal(4900L));
		final ResponseEntity<Void> responseBigTx = sendRequest(bigTx);
		assertTrue(HttpStatus.OK.equals(responseBigTx.getStatusCode()));
	}

	private ResponseEntity<Void> sendRequest(final TransactionRequest tr) {
		final ResponseEntity<Void> response = restTemplate.exchange(getUri("/v1/transactions"), HttpMethod.POST,
				new HttpEntity<>(tr), Void.class);
		return response;
	}
	
	private ResponseEntity<Void> sendPayment(final TransactionRequest tr) {
		final ResponseEntity<Void> response = restTemplate.exchange(getUri("/v1/payments"), HttpMethod.POST,
				new HttpEntity<>(Arrays.asList(tr)), Void.class);
		return response;
	}

}
