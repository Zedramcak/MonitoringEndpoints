package eu.adamzrcek.endpointmonitoringapi.repositories;

import eu.adamzrcek.endpointmonitoringapi.models.MonitoredEndpoint;
import org.springframework.data.repository.CrudRepository;

public interface MonitoringEndpointRepository extends CrudRepository<MonitoredEndpoint, Integer> {

}
