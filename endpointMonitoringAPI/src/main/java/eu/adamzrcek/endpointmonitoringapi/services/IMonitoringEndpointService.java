package eu.adamzrcek.endpointmonitoringapi.services;

import eu.adamzrcek.endpointmonitoringapi.models.MonitoredEndpoint;
import eu.adamzrcek.endpointmonitoringapi.models.User;

import java.util.List;

public interface IMonitoringEndpointService {
    List<MonitoredEndpoint> getMonitoredEndpointsForUser(User owner);
    void createNewMonitoredEndpoint(User owner);
    void updateMonitoredEndpoint(MonitoredEndpoint endpointToUpdate);
    void deleteMonitoredEndpoint(MonitoredEndpoint endpointToDelete);
    MonitoredEndpoint getMonitoredEndpoint(int id, User owner);
}
