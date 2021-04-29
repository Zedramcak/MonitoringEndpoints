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
        newMonitoredEndpoint.setDateOfCreation(Timestamp.valueOf(LocalDateTime.now()));
        newMonitoredEndpoint = repository.save(newMonitoredEndpoint);
        newMonitoredEndpoint.setUrl("/monitoredEndpoint/" + newMonitoredEndpoint.getId());
        return repository.save(newMonitoredEndpoint);
    }

    @Override
    public MonitoredEndpoint updateMonitoredEndpoint(int id, MonitoredEndpoint endpointToUpdate) {
        getMonitoredEndpoint(id).setName(endpointToUpdate.getName());
        endpointToUpdate = getMonitoredEndpoint(id);
        endpointToUpdate.setDateOfLastCheck(Timestamp.valueOf(LocalDateTime.now()));
        endpointToUpdate.setMonitoredInterval(endpointToUpdate.getDateOfLastCheck().compareTo(Timestamp.valueOf(LocalDateTime.now())));
        return repository.save(endpointToUpdate);
    }

    @Override
    public void deleteMonitoredEndpoint(MonitoredEndpoint endpointToDelete) {
        repository.delete(endpointToDelete);
    }

    @Override
    public MonitoredEndpoint getMonitoredEndpoint(int id) {
        MonitoredEndpoint endpointToFind = repository.getMonitoredEndpointById(id);
        Timestamp localTime = Timestamp.valueOf(LocalDateTime.now());
        if (endpointToFind == null) {
            return null;
        }
        if (endpointToFind.getDateOfLastCheck() == null) {
            endpointToFind.setDateOfLastCheck(localTime);
            endpointToFind.setMonitoredInterval(
                    (int) Duration.between(
                            endpointToFind.getDateOfCreation().toLocalDateTime(),
                            localTime.toLocalDateTime()
                    ).toSeconds()
            );
        } else {
            endpointToFind.setMonitoredInterval((int) Duration.between(
                    endpointToFind.getDateOfLastCheck().toLocalDateTime(),
                    localTime.toLocalDateTime()
            ).toSeconds());
            endpointToFind.setDateOfLastCheck(localTime);
        }
        return endpointToFind;
    }
}
