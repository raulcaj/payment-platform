package br.com.raulcaj.transactionmodule.domain;

import org.springframework.data.repository.Repository;

public interface PaymentsTrackingRepository extends Repository<PaymentsTracking, Long> {
	public void save(PaymentsTracking pt);
}
