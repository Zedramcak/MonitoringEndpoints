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
    private final Logger logger = LoggerFactory.getLogger(MonitoredEndpointController.class);

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


        if (isTheUserOwnerOfThisEndpoint(accessToken, getEndpoint)){
            return logAddNewMonitoringResultAndReturnTheEndpoint(getEndpoint);
        }
        else throw logCreateMonitoringResultAndThrowForbiddenException(getEndpoint, "read");
    }



    @PutMapping("/{id}")
    MonitoredEndpoint updateMonitoredEndpoint(@RequestHeader(value = "accessToken") String accessToken,
                                              @PathVariable String id,
                                              @RequestBody MonitoredEndpoint monitoredEndpointToUpdate){

        MonitoredEndpoint monitoredEndpoint = monitoredEndpointService.getMonitoredEndpoint(Integer.parseInt(id));

        if (isTheUserOwnerOfThisEndpoint(accessToken, monitoredEndpoint))
            return logAddNewMonitoringResultAndReturnTheEndpoint(
                    monitoredEndpointService.updateMonitoredEndpoint(Integer.parseInt(id), monitoredEndpointToUpdate)
            );
        else throw logCreateMonitoringResultAndThrowForbiddenException(monitoredEndpoint, "update");
    }

    @DeleteMapping("/{id}")
    void deleteMonitoredEndpoint(@PathVariable String id, @RequestHeader(value = "accessToken") String accessToken){
        int parsedId = Integer.parseInt(id);
        MonitoredEndpoint endpointToDelete = monitoredEndpointService.getMonitoredEndpoint(parsedId);

        if (isTheUserOwnerOfThisEndpoint(accessToken, endpointToDelete))
            monitoredEndpointService
                    .deleteMonitoredEndpoint(monitoredEndpointService.getMonitoredEndpoint(parsedId));
        else throw logCreateMonitoringResultAndThrowForbiddenException(endpointToDelete, "delete");
    }

    private MonitoredEndpoint logAddNewMonitoringResultAndReturnTheEndpoint(MonitoredEndpoint getEndpoint) {
        logger.info("Status code: " + HttpStatus.OK.value() + ", payload: " + getEndpoint);
        monitoringResultService.createNewMonitoredResultForMonitoredEndpoint(
                getEndpoint, HttpStatus.OK.value(), getEndpoint.toString()
        );
        return getEndpoint;
    }

    private boolean isTheUserOwnerOfThisEndpoint(@RequestHeader("accessToken") String accessToken, MonitoredEndpoint getEndpoint) {
        return getEndpoint.getOwner().equals(userService.getUserByToken(accessToken));
    }

    private UnauthorizedException logCreateMonitoringResultAndThrowForbiddenException(MonitoredEndpoint getEndpoint, String message) {
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
