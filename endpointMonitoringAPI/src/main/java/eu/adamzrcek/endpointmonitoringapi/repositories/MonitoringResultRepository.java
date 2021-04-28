package eu.adamzrcek.endpointmonitoringapi.repositories;

import eu.adamzrcek.endpointmonitoringapi.models.MonitoredEndpoint;
import eu.adamzrcek.endpointmonitoringapi.models.MonitoringResult;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MonitoringResultRepository extends CrudRepository<MonitoringResult, Integer> {
    List<MonitoringResult> getFirst10ByMonitoredEndpointOrderByDateOfCheckDesc(MonitoredEndpoint monitoredEndpoint);
}
