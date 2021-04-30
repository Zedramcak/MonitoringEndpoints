package eu.adamzrcek.endpointmonitoringapi.services;

import eu.adamzrcek.endpointmonitoringapi.models.User;
import eu.adamzrcek.endpointmonitoringapi.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService {

    private final UserRepository repository;
    Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User getUserByToken(String token) {
        User userToFind = repository.getUserByAccessToken(token);
        if (userToFind == null) {
            logger.info("User with provided token not found: " + token);
            return null;
        }
        return userToFind;
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
