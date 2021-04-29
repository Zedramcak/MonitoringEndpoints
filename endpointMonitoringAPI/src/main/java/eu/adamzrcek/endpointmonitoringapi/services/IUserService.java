package eu.adamzrcek.endpointmonitoringapi.services;

import eu.adamzrcek.endpointmonitoringapi.models.User;

import java.util.List;

public interface IUserService {
    User getUserByToken(String token);

    List<User> findAll();

    void addNewUser(User user);
}
