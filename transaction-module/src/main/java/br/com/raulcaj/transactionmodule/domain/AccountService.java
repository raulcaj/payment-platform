package br.com.raulcaj.transactionmodule.domain;

import java.net.URI;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AccountService {

	@Value("service.account.path.getById")
	private String getByIdPath;
	
	@Value("service.account.path.patchLimit")
	private String patchLimitPath;
	
	@Inject
	private DiscoveryClient discoveryClient;
	
	@Value("service.account.name")
	private String accountServiceName;
	
	private RestTemplate restTemplate = new RestTemplate();
	
	private String operationUri(final String operationPath) {
		final Optional<ServiceInstance> service = discoveryClient.getInstances(accountServiceName).stream().findFirst();
		return service.map(ServiceInstance::getUri).map(Object::toString).orElse("http://localhost:8080").concat(operationPath);
	}
	
	public void transactionRequested(TxRequest tx) throws Exception {
		final ResponseEntity<Void> response = restTemplate.postForObject(operationUri(patchLimitPath), tx, ResponseEntity.class);
		if(!HttpStatus.ACCEPTED.equals(response.getStatusCode())) {
			throw new Exception("Transaction out of account limit");
		}
	}

}
