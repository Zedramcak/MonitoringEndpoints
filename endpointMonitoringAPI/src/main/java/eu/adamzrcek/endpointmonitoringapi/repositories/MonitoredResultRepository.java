package eu.adamzrcek.endpointmonitoringapi.repositories;

import eu.adamzrcek.endpointmonitoringapi.models.MonitoredEndpoint;
import eu.adamzrcek.endpointmonitoringapi.models.MonitoredResult;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MonitoredResultRepository extends CrudRepository<MonitoredResult, Integer> {
    List<MonitoredResult> getFirst10ByMonitoredEndpointOrderByDateOfCheckDesc(MonitoredEndpoint monitoredEndpoint);
}
