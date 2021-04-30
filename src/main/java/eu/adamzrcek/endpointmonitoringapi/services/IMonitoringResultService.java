package eu.adamzrcek.endpointmonitoringapi.services;

import eu.adamzrcek.endpointmonitoringapi.models.MonitoredEndpoint;
import eu.adamzrcek.endpointmonitoringapi.models.MonitoringResult;

import java.util.List;

public interface IMonitoringResultService {
    List<MonitoringResult> getLastTenMonitoredResultsForMonitoredEndpoint(MonitoredEndpoint monitoredEndpoint);

    MonitoringResult createNewMonitoredResultForMonitoredEndpoint(MonitoredEndpoint monitoredEndpoint, int statusCode, String payload);

    void deleteAllMonitoredResultsForMonitoredEndpoint(MonitoredEndpoint monitoredEndpoint);
}
