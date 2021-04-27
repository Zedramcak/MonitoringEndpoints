package eu.adamzrcek.endpointmonitoringapi.services;

import eu.adamzrcek.endpointmonitoringapi.models.MonitoredEndpoint;
import eu.adamzrcek.endpointmonitoringapi.models.MonitoredResult;
import eu.adamzrcek.endpointmonitoringapi.repositories.MonitoredResultRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class MonitoringResultService implements IMonitoringResultService {

    @Autowired
    private MonitoredResultRepository repository;

    @Override
    public List<MonitoredResult> getLastTenMonitoredResultsForMonitoredEndpoint(MonitoredEndpoint monitoredEndpoint) {
        return repository.getFirst10ByMonitoredEndpointOrderByDateOfCheckDesc(monitoredEndpoint);
    }

    @Override
    public void createNewMonitoredResultForMonitoredEndpoint(MonitoredEndpoint monitoredEndpoint, int statusCode) {
        MonitoredResult newMonitoredResult = new MonitoredResult();
        newMonitoredResult.setMonitoredEndpoint(monitoredEndpoint);
        newMonitoredResult.setReturnedPayload(monitoredEndpoint.getName());
        newMonitoredResult.setStatusCode(statusCode);
        newMonitoredResult.setDateOfCheck(Date.valueOf(LocalDate.now()));

        repository.save(newMonitoredResult);
    }
}
