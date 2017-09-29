package br.com.raulcaj.transactionmodule.domain;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class AccountService {

	@Value("${service.account.path.getById}")
	private String getByIdPath;

	@Value("${service.account.path.patchLimit}")
	private String patchLimitPath;

	@Inject
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

	private String operationUri(final String operationPath) {
		final Optional<ServiceInstance> service = discoveryClient.getInstances(accountServiceName).stream().findFirst();
		return service.map(ServiceInstance::getUri).map(Object::toString).orElse("http://localhost:8080")
				.concat(operationPath);
	}

	public boolean canTransactionProceed(TxRequest tx, OperationType operationType) throws Exception {
		final Map<String, Object> request = new LinkedHashMap<>();
		request.put("limit_type", operationType.getLimit_type());
		request.put("amount", tx.getAmount());
		try {
			restTemplate.patchForObject(operationUri(patchLimitPath), Arrays.asList(request), Object.class, tx.getAccount_id());
			return true;
		} catch (HttpClientErrorException e) {
			if (HttpStatus.NOT_ACCEPTABLE.equals(e.getStatusCode())) {
				return false;
			}
		}
		return false;
	}

}
