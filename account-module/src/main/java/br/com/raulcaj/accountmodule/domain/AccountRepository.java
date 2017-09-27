package br.com.raulcaj.accountmodule.domain;

import java.util.Optional;

import org.springframework.data.repository.Repository;

public interface AccountRepository extends Repository<Account, Long> {
	Optional<Account> findOne(Long id);
}
