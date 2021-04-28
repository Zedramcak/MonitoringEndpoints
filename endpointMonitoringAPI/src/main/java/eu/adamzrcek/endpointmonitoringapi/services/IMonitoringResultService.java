package eu.adamzrcek.endpointmonitoringapi.services;

import eu.adamzrcek.endpointmonitoringapi.models.MonitoredEndpoint;
import eu.adamzrcek.endpointmonitoringapi.models.MonitoringResult;
import java.util.List;

public interface IMonitoringResultService {
    List<MonitoringResult> getLastTenMonitoredResultsForMonitoredEndpoint(MonitoredEndpoint monitoredEndpoint);
    void createNewMonitoredResultForMonitoredEndpoint(MonitoredEndpoint monitoredEndpoint, int statusCode);
}
