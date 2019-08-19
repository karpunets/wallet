package ee.avalanchelabs.walletserver.repository;

import ee.avalanchelabs.walletserver.model.entity.AccountTransaction;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface TransactionRepository extends CrudRepository<AccountTransaction, UUID> {
}
