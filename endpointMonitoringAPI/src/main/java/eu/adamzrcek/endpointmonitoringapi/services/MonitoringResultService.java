package eu.adamzrcek.endpointmonitoringapi.services;

import eu.adamzrcek.endpointmonitoringapi.models.MonitoredEndpoint;
import eu.adamzrcek.endpointmonitoringapi.models.MonitoringResult;
import eu.adamzrcek.endpointmonitoringapi.repositories.MonitoringResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class MonitoringResultService implements IMonitoringResultService {

    @Autowired
    private MonitoringResultRepository repository;

    @Override
    public List<MonitoringResult> getLastTenMonitoredResultsForMonitoredEndpoint(MonitoredEndpoint monitoredEndpoint) {
        return repository.getFirst10ByMonitoredEndpointOrderByDateOfCheckDesc(monitoredEndpoint);
    }

    @Override
    public void createNewMonitoredResultForMonitoredEndpoint(MonitoredEndpoint monitoredEndpoint, int statusCode) {
        MonitoringResult newMonitoringResult = new MonitoringResult();
        newMonitoringResult.setMonitoredEndpoint(monitoredEndpoint);
        newMonitoringResult.setReturnedPayload(monitoredEndpoint.getName());
        newMonitoringResult.setStatusCode(statusCode);
        newMonitoringResult.setDateOfCheck(Date.valueOf(LocalDate.now()));

        repository.save(newMonitoringResult);
    }
}
