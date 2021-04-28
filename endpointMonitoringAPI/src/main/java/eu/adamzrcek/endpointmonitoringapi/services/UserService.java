package eu.adamzrcek.endpointmonitoringapi.services;

import eu.adamzrcek.endpointmonitoringapi.models.User;
import eu.adamzrcek.endpointmonitoringapi.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    private final UserRepository repository;
    Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User getUserByToken(String token) {
        Optional<User> userToFind = repository.findByAccessToken(token);
        if (userToFind.isEmpty()){
            logger.debug("User with provided token not found: " + token);
            return null;
        }
        return userToFind.get();
    }

    @Override
    public List<User> findAll() {
        return (List<User>) repository.findAll();
    }

    @Override
    public void addNewUser(User user) {
        repository.save(user);
    }
}
