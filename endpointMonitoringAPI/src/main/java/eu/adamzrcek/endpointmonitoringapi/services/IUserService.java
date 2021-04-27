package eu.adamzrcek.endpointmonitoringapi.services;

import eu.adamzrcek.endpointmonitoringapi.models.User;

import java.util.Optional;

public interface IUserService {
    User getUserByToken(String token);
}
