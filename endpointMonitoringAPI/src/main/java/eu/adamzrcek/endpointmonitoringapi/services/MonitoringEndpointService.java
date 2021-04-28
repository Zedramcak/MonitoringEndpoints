package eu.adamzrcek.endpointmonitoringapi.services;

import eu.adamzrcek.endpointmonitoringapi.models.MonitoredEndpoint;
import eu.adamzrcek.endpointmonitoringapi.models.User;
import eu.adamzrcek.endpointmonitoringapi.repositories.MonitoringEndpointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class MonitoringEndpointService implements IMonitoringEndpointService {

    private MonitoringEndpointRepository repository;

    @Override
    public List<MonitoredEndpoint> getMonitoredEndpointsForUser(User owner) {
        return repository.getMonitoredEndpointByOwner(owner);
    }

    @Override
    public void createNewMonitoredEndpoint(User owner) {
        MonitoredEndpoint newMonitoredEndpoint = new MonitoredEndpoint();
        newMonitoredEndpoint.setOwner(owner);
        newMonitoredEndpoint.setDateOfCreation(Date.valueOf(LocalDate.now()));
        newMonitoredEndpoint.setUrl("/monitoredEndpoint/");
        newMonitoredEndpoint.setName("Test name");
        newMonitoredEndpoint.setDateOfLastCheck(null);

        repository.save(newMonitoredEndpoint);
    }

    @Override
    public void updateMonitoredEndpoint(MonitoredEndpoint endpointToUpdate) {
        repository.save(endpointToUpdate);
    }

    @Override
    public void deleteMonitoredEndpoint(MonitoredEndpoint endpointToDelete) {
        repository.delete(endpointToDelete);
    }

    @Override
    public MonitoredEndpoint getMonitoredEndpoint(int id, User owner) {
        MonitoredEndpoint monitoredEndpoint = repository.getMonitoredEndpointById(id);
        if (monitoredEndpoint.getOwner().getId() == owner.getId()) return monitoredEndpoint;
        else return null;
    }
}
