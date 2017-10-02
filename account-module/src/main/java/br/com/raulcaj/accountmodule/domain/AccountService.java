package br.com.raulcaj.accountmodule.domain;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.raulcaj.accountmodule.controllers.NotAcceptableException;
import br.com.raulcaj.accountmodule.controllers.NotFoundException;

@Service
public class AccountService {

	@Value("${error.message.cannot_update_limit}")
	private String cannotUpdateLimit;
	
	@Autowired
	private AccountRepository accountRepository;

	public Optional<Account> findById(@NotNull Long id) {
		return accountRepository.findOne(id);
	}

	public List<Account> findAll() {
		return accountRepository.findAll();
	}

	public void updateLimits(final Long id, final Account patchRequest) throws Exception {
		ifPresentOrElseThrow(this.findById(id), a -> {
			a.updateLimit(patchRequest);
			try {
				accountRepository.save(a);
			} catch (Exception e) {
				throw new NotAcceptableException(cannotUpdateLimit);
			}
		}, NotFoundException::new);
	}

	@FunctionalInterface
	private interface ThrowableConsumer<T> {
		void accept(T t) throws Exception;
	}

	private <T, E extends Exception> void ifPresentOrElseThrow(Optional<T> operand, ThrowableConsumer<T> code,
			Supplier<E> ex) throws Exception {
		if (operand.isPresent()) {
			code.accept(operand.get());
		} else {
			throw ex.get();
		}
	}
}
