package eu.adamzrcek.endpointmonitoringapi.repositories;

import eu.adamzrcek.endpointmonitoringapi.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
    User getUserByAccessToken(String token);
}
