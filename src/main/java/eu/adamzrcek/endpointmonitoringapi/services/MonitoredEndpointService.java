package eu.adamzrcek.endpointmonitoringapi.services;

import eu.adamzrcek.endpointmonitoringapi.models.MonitoredEndpoint;
import eu.adamzrcek.endpointmonitoringapi.models.User;
import eu.adamzrcek.endpointmonitoringapi.repositories.MonitoredEndpointRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MonitoredEndpointService implements IMonitoredEndpointService {

    private final MonitoredEndpointRepository repository;

    public MonitoredEndpointService(MonitoredEndpointRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<MonitoredEndpoint> getMonitoredEndpointsForUser(User owner) {
        return new ArrayList<>(repository.findAllByOwner(owner));
    }

    @Override
    public MonitoredEndpoint createNewMonitoredEndpoint(MonitoredEndpoint newMonitoredEndpoint) {
        newMonitoredEndpoint.setDateOfCreation(getTimestampOfLocalDateTime());
        newMonitoredEndpoint = repository.save(newMonitoredEndpoint);
        return repository.save(newMonitoredEndpoint);
    }

    @Override
    public MonitoredEndpoint updateMonitoredEndpoint(int id, MonitoredEndpoint endpointToUpdate) {
        MonitoredEndpoint monitoredEndpoint = repository.getMonitoredEndpointById(id);
        monitoredEndpoint.setUrl(endpointToUpdate.getUrl());
        monitoredEndpoint.setName(endpointToUpdate.getName());
        return repository.save(monitoredEndpoint);
    }

    @Override
    public void deleteMonitoredEndpoint(MonitoredEndpoint endpointToDelete) {
        repository.delete(endpointToDelete);
    }

    @Override
    public MonitoredEndpoint getMonitoredEndpoint(int id) {
        MonitoredEndpoint endpointToFind = repository.getMonitoredEndpointById(id);
        if (endpointToFind == null) return null;
        setMonitoredIntervalAndLastCheck(endpointToFind);
        return endpointToFind;
    }

    @Override
    public void setMonitoredIntervalAndLastCheck(MonitoredEndpoint monitoredEndpoint) {
        Timestamp localTime = getTimestampOfLocalDateTime();
        if (monitoredEndpoint.getDateOfLastCheck() == null)
            setMonitoredIntervalAndLastCheckIfItHasNotBeenSetBefore(monitoredEndpoint, localTime);
        else setMonitoredIntervalAndLastCheckIfItHasBennSetBefore(monitoredEndpoint, localTime);
    }

    private void setMonitoredIntervalAndLastCheckIfItHasBennSetBefore(MonitoredEndpoint monitoredEndpoint, Timestamp localTime) {
        monitoredEndpoint.setMonitoredInterval(
                (int) Duration.between(
                        monitoredEndpoint.getDateOfLastCheck().toLocalDateTime(),
                        localTime.toLocalDateTime()
                ).toSeconds());
        monitoredEndpoint.setDateOfLastCheck(localTime);
    }

    private void setMonitoredIntervalAndLastCheckIfItHasNotBeenSetBefore(MonitoredEndpoint monitoredEndpoint, Timestamp localTime) {
        monitoredEndpoint.setDateOfLastCheck(localTime);
        monitoredEndpoint.setMonitoredInterval(
                (int) Duration.between(
                        monitoredEndpoint.getDateOfCreation().toLocalDateTime(),
                        localTime.toLocalDateTime()
                ).toSeconds()
        );
    }

    private Timestamp getTimestampOfLocalDateTime() {
        return Timestamp.valueOf(LocalDateTime.now());
    }
}
