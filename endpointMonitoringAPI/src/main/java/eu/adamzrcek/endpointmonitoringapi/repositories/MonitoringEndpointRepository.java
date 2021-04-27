package eu.adamzrcek.endpointmonitoringapi.repositories;

import eu.adamzrcek.endpointmonitoringapi.models.MonitoredEndpoint;
import eu.adamzrcek.endpointmonitoringapi.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MonitoringEndpointRepository extends CrudRepository<MonitoredEndpoint, Integer> {
    List<MonitoredEndpoint> getMonitoredEndpointByOwner(User owner);
    MonitoredEndpoint getMonitoredEndpointById(int id);
}
