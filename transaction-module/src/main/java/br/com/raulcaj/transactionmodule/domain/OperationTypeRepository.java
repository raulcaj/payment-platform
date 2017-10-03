package br.com.raulcaj.transactionmodule.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.Repository;

public interface OperationTypeRepository extends Repository<OperationType, Long> {
	Optional<OperationType> findOne(Long id);
	List<OperationType> findAll();
	boolean exists(Long operationTypeId);
}
