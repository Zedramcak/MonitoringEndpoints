package eu.adamzrcek.endpointmonitoringapi.repositories;

import eu.adamzrcek.endpointmonitoringapi.models.MonitoringResult;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MonitoringResultRepository extends PagingAndSortingRepository<MonitoringResult, Integer> {
}
