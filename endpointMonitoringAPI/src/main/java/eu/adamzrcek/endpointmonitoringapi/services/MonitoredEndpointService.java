package eu.adamzrcek.endpointmonitoringapi.services;

import eu.adamzrcek.endpointmonitoringapi.models.MonitoredEndpoint;
import eu.adamzrcek.endpointmonitoringapi.models.User;
import eu.adamzrcek.endpointmonitoringapi.repositories.MonitoredEndpointRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
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
        newMonitoredEndpoint.setDateOfCreation(Date.valueOf(LocalDate.now()));
        newMonitoredEndpoint = repository.save(newMonitoredEndpoint);
        newMonitoredEndpoint.setUrl("/monitoredEndpoint/" + newMonitoredEndpoint.getId());
        return repository.save(newMonitoredEndpoint);
    }

    @Override
    public MonitoredEndpoint updateMonitoredEndpoint(int id, MonitoredEndpoint endpointToUpdate) {
        getMonitoredEndpoint(id).setName(endpointToUpdate.getName());
        endpointToUpdate = getMonitoredEndpoint(id);
        endpointToUpdate.setDateOfLastCheck(Date.valueOf(LocalDate.now()));
        return repository.save(endpointToUpdate);
    }

    @Override
    public void deleteMonitoredEndpoint(MonitoredEndpoint endpointToDelete) {
        repository.delete(endpointToDelete);
    }

    @Override
    public MonitoredEndpoint getMonitoredEndpoint(int id) {
        MonitoredEndpoint endpointToFind = repository.getMonitoredEndpointById(id);
        if (endpointToFind == null){
            return null;
        }
        endpointToFind.setDateOfLastCheck(Date.valueOf(LocalDate.now()));
        return endpointToFind;
    }
}
