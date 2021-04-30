package eu.adamzrcek.endpointmonitoringapi.services;

import eu.adamzrcek.endpointmonitoringapi.models.MonitoredEndpoint;
import eu.adamzrcek.endpointmonitoringapi.models.User;

import java.util.List;

public interface IMonitoredEndpointService {
    List<MonitoredEndpoint> getMonitoredEndpointsForUser(User owner);

    MonitoredEndpoint createNewMonitoredEndpoint(MonitoredEndpoint newMonitoredEndpoint);

    MonitoredEndpoint updateMonitoredEndpoint(int id, MonitoredEndpoint endpointToUpdate);

    void deleteMonitoredEndpoint(MonitoredEndpoint endpointToDelete);

    MonitoredEndpoint getMonitoredEndpoint(int id);
}
