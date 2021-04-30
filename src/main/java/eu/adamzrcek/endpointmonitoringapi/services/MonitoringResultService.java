package eu.adamzrcek.endpointmonitoringapi.services;

import eu.adamzrcek.endpointmonitoringapi.models.MonitoredEndpoint;
import eu.adamzrcek.endpointmonitoringapi.models.MonitoringResult;
import eu.adamzrcek.endpointmonitoringapi.repositories.MonitoringResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MonitoringResultService implements IMonitoringResultService {

    private final MonitoringResultRepository repository;

    public MonitoringResultService(MonitoringResultRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<MonitoringResult> getLastTenMonitoredResultsForMonitoredEndpoint(MonitoredEndpoint monitoredEndpoint) {
        return repository.findTop10ByMonitoredEndpointOrderByIdDesc(monitoredEndpoint);
    }

    @Override
    public MonitoringResult createNewMonitoredResultForMonitoredEndpoint(MonitoredEndpoint monitoredEndpoint, int statusCode, String payload) {
        MonitoringResult newMonitoringResult = new MonitoringResult();
        newMonitoringResult.setMonitoredEndpoint(monitoredEndpoint);
        newMonitoringResult.setReturnedPayload(payload);
        newMonitoringResult.setStatusCode(statusCode);
        newMonitoringResult.setDateOfCheck(Timestamp.valueOf(LocalDateTime.now()));

        return repository.save(newMonitoringResult);
    }

    @Override
    @Transactional
    public void deleteAllMonitoredResultsForMonitoredEndpoint(MonitoredEndpoint monitoredEndpoint) {
        repository.deleteAllByMonitoredEndpoint(monitoredEndpoint);
    }
}
