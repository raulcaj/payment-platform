package br.com.raulcaj.transactionmodule.domain;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import br.com.raulcaj.transactionmodule.controller.NotAcceptableException;

@Service
public class AccountService {

	@Value("${service.account.path.getById}")
	private String getByIdPath;

	@Value("${service.account.path.patchLimit}")
	private String patchLimitPath;

	@Autowired
	private DiscoveryClient discoveryClient;

	@Value("${service.account.name}")
	private String accountServiceName;

	private RestTemplate restTemplate = createRestTemplate();

	private RestTemplate createRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		// 4 debug purposes
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectTimeout(50000);
		requestFactory.setReadTimeout(50000);
		restTemplate.setRequestFactory(requestFactory);
		return restTemplate;
	}

	private String getUri(final String operationPath) {
		final Optional<ServiceInstance> service = discoveryClient.getInstances(accountServiceName).stream().findFirst();
		return service.map(ServiceInstance::getUri).map(Object::toString).orElse("http://localhost:8080")
				.concat(operationPath);
	}
	
	public void executeAccountLimitUpdate(final Long accountId, final Pair<BigDecimal, BigDecimal> amountSpent) throws NotAcceptableException {
		final AccountRequest accountInfo = AccountRequest.createAccountRequest(accountId, amountSpent);
		try {
			final ResponseEntity<Void> response = restTemplate.exchange(getUri(patchLimitPath), HttpMethod.PATCH,
					new HttpEntity<>(accountInfo), Void.class, accountId);
			if (!HttpStatus.OK.equals(response.getStatusCode())) {
				throw new NotAcceptableException("Could not update account limit");
			}
		} catch (HttpClientErrorException e) {
			if (HttpStatus.NOT_ACCEPTABLE.equals(e.getStatusCode())) {
				throw new NotAcceptableException("Limit exceeded");
			}
			throw e;
		}
	}

	public boolean accountNotExist(Long accountId) throws NotAcceptableException {
		final ResponseEntity<AccountRequest> response = restTemplate.exchange(getUri(getByIdPath), HttpMethod.GET, null,
				AccountRequest.class, accountId);
		if (HttpStatus.NOT_FOUND.equals(response.getStatusCode())) {
			return true;
		}
		if (HttpStatus.OK.equals(response.getStatusCode())) {
			return !accountId.equals(response.getBody().getId());
		}
		throw new NotAcceptableException("Error trying to look for this account");
	}

}
