package eu.adamzrcek.endpointmonitoringapi.services;

import eu.adamzrcek.endpointmonitoringapi.models.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    User getUserByToken(String token);
    List<User> findAll();
    void addNewUser(User user);
}
