package ee.avalanchelabs.walletserver.repository;

import ee.avalanchelabs.walletserver.model.AccountCurrency;
import ee.avalanchelabs.walletserver.model.entity.Account;
import ee.avalanchelabs.walletserver.model.entity.User;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends CrudRepository<Account, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findByUserAndCurrency(User user, AccountCurrency currency);

    List<Account> findAllByUser(User user);
}
