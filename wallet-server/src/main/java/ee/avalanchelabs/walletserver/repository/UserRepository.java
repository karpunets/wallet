package ee.avalanchelabs.walletserver.repository;

import ee.avalanchelabs.walletserver.model.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
}
