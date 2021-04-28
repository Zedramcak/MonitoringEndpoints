package eu.adamzrcek.endpointmonitoringapi.controllers;

import eu.adamzrcek.endpointmonitoringapi.models.MonitoredEndpoint;
import eu.adamzrcek.endpointmonitoringapi.services.MonitoredEndpointService;
import eu.adamzrcek.endpointmonitoringapi.services.MonitoringResultService;
import eu.adamzrcek.endpointmonitoringapi.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/monitoredEndpoint")
public class MonitoredEndpointController {

    private final MonitoredEndpointService monitoredEndpointService;
    private final MonitoringResultService monitoringResultService;
    private final UserService userService;
    private Logger logger = LoggerFactory.getLogger(MonitoredEndpointController.class);

    public MonitoredEndpointController(MonitoredEndpointService monitoredEndpointService, MonitoringResultService monitoringResultService, UserService userService) {
        this.monitoredEndpointService = monitoredEndpointService;
        this.monitoringResultService = monitoringResultService;
        this.userService = userService;
    }

    @GetMapping("/")
    List<MonitoredEndpoint> getAllEndpoints(@RequestHeader(value = "accessToken") String accessToken){
        return monitoredEndpointService.getMonitoredEndpointsForUser(userService.getUserByToken(accessToken));
    }

    @PostMapping("/")
    MonitoredEndpoint addNewEndpoint(@RequestHeader(value = "accessToken") String accessToken,
                                     @RequestBody MonitoredEndpoint newMonitoredEndpoint){
        newMonitoredEndpoint.setOwner(userService.getUserByToken(accessToken));
        return monitoredEndpointService.createNewMonitoredEndpoint(newMonitoredEndpoint);
    }

    @GetMapping("/{id}")
    MonitoredEndpoint getMonitoredEndpoint(@PathVariable String id,
                                           @RequestHeader(value = "accessToken") String accessToken){

        MonitoredEndpoint getEndpoint = monitoredEndpointService.getMonitoredEndpoint( Integer.parseInt(id));


        if (getEndpoint.getOwner().equals(userService.getUserByToken(accessToken))){
            logger.info("Status code: " + HttpStatus.OK.value() + ", payload: " + getEndpoint);
            monitoringResultService.createNewMonitoredResultForMonitoredEndpoint(
                    getEndpoint, HttpStatus.OK.value(), getEndpoint.toString()
            );
            return getEndpoint;
        }
        else throw createForbiddenException(getEndpoint, "read");
    }


    @PutMapping("/{id}")
    MonitoredEndpoint updateMonitoredEndpoint(@RequestHeader(value = "accessToken") String accessToken,
                                              @PathVariable String id,
                                              @RequestBody MonitoredEndpoint monitoredEndpointToUpdate){

        MonitoredEndpoint monitoredEndpoint = monitoredEndpointService.getMonitoredEndpoint(Integer.parseInt(id));

        if (monitoredEndpoint.getOwner().equals(userService.getUserByToken(accessToken))) {
            return monitoredEndpointService.updateMonitoredEndpoint(Integer.parseInt(id), monitoredEndpointToUpdate);
        }
        else throw createForbiddenException(monitoredEndpoint, "update");
    }

    @DeleteMapping("/{id}")
    void deleteMonitoredEndpoint(@PathVariable String id, @RequestHeader(value = "accessToken") String accessToken){
        int parsedId = Integer.parseInt(id);
        MonitoredEndpoint endpointToDelete = monitoredEndpointService.getMonitoredEndpoint(parsedId);

        if (endpointToDelete.getOwner().equals(userService.getUserByToken(accessToken)))
            monitoredEndpointService
                    .deleteMonitoredEndpoint(monitoredEndpointService.getMonitoredEndpoint(parsedId));
        else throw createForbiddenException(endpointToDelete, "delete");
    }

    private UnauthorizedException createForbiddenException(MonitoredEndpoint getEndpoint, String message) {
        UnauthorizedException unauthorizedException = new UnauthorizedException(message);
        logger.info("Status code: " + HttpStatus.FORBIDDEN.value() + ", payload: " + unauthorizedException);
        monitoringResultService.createNewMonitoredResultForMonitoredEndpoint(
                getEndpoint, HttpStatus.FORBIDDEN.value(), unauthorizedException.toString()
        );
        throw unauthorizedException;
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    private static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message){
            super(String.format("You are not allowed to %S this endpoint", message));
        }

    }
}
