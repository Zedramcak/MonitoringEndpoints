package eu.adamzrcek.endpointmonitoringapi.controllers;

import eu.adamzrcek.endpointmonitoringapi.models.MonitoredEndpoint;
import eu.adamzrcek.endpointmonitoringapi.models.MonitoringResult;
import eu.adamzrcek.endpointmonitoringapi.services.MonitoredEndpointService;
import eu.adamzrcek.endpointmonitoringapi.services.MonitoringResultService;
import eu.adamzrcek.endpointmonitoringapi.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/monitoredEndpoint")
public class MonitoredEndpointController {

    private final MonitoredEndpointService monitoredEndpointService;
    private final MonitoringResultService monitoringResultService;
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(MonitoredEndpointController.class);

    public MonitoredEndpointController(MonitoredEndpointService monitoredEndpointService,
                                       MonitoringResultService monitoringResultService,
                                       UserService userService) {
        this.monitoredEndpointService = monitoredEndpointService;
        this.monitoringResultService = monitoringResultService;
        this.userService = userService;
    }

    @GetMapping("")
    List<MonitoredEndpoint> getAllEndpoints(@RequestHeader(value = "accessToken") String accessToken) throws InvalidToken, NoSuchUser {
        checkAccessToken(accessToken);

        return monitoredEndpointService.getMonitoredEndpointsForUser(userService.getUserByToken(accessToken));
    }

    @PostMapping("")
    MonitoredEndpoint addNewEndpoint(@RequestHeader(value = "accessToken") String accessToken,
                                     @RequestBody MonitoredEndpoint newMonitoredEndpoint,
                                     HttpServletResponse response) throws InvalidToken, NoSuchUser {
        checkAccessToken(accessToken);

        newMonitoredEndpoint.setOwner(userService.getUserByToken(accessToken));
        MonitoredEndpoint createdEndpoint = monitoredEndpointService.createNewMonitoredEndpoint(newMonitoredEndpoint);
        logger.info("Status code: " + HttpStatus.CREATED.value() + ", payload: " + createdEndpoint);
        monitoringResultService.createNewMonitoredResultForMonitoredEndpoint(createdEndpoint,
                HttpStatus.CREATED.value(),
                createdEndpoint.toString());
        response.setStatus(HttpServletResponse.SC_CREATED);
        return createdEndpoint;
    }

    @GetMapping("/{id}")
    MonitoredEndpoint getMonitoredEndpoint(@PathVariable String id,
                                           @RequestHeader(value = "accessToken") String accessToken) throws InvalidPath, InvalidToken, NoSuchUser {
        checkAccessToken(accessToken);
        int parsedId = tryParseIdThrowExceptionIfUnableToParse(id);

        MonitoredEndpoint getEndpoint = ifEndpointExistReturnItElseThrowException(parsedId);
        if (isTheUserOwnerOfThisEndpoint(accessToken, getEndpoint)) {
            return logAddNewMonitoringResultAndReturnTheEndpoint(getEndpoint);
        } else throw logCreateMonitoringResultAndThrowForbiddenException(getEndpoint, "read");
    }

    @GetMapping("/{id}/monitoringResults")
    List<MonitoringResult> getMonitoringResultsForMonitoredEndpoint(@PathVariable String id,
                                                                    @RequestHeader(value = "accessToken") String accessToken) throws InvalidPath, InvalidToken, NoSuchUser {
        checkAccessToken(accessToken);
        int parsedId = tryParseIdThrowExceptionIfUnableToParse(id);

        MonitoredEndpoint endpoint = ifEndpointExistReturnItElseThrowException(parsedId);
        if (isTheUserOwnerOfThisEndpoint(accessToken, endpoint))
            return monitoringResultService.getLastTenMonitoredResultsForMonitoredEndpoint(endpoint);
        else throw logCreateMonitoringResultAndThrowForbiddenException(endpoint, "read");
    }


    @PutMapping("/{id}")
    MonitoredEndpoint updateMonitoredEndpoint(@RequestHeader(value = "accessToken") String accessToken,
                                              @PathVariable String id,
                                              @RequestBody MonitoredEndpoint monitoredEndpointToUpdate) throws InvalidPath, InvalidToken, NoSuchUser {
        checkAccessToken(accessToken);

        int parsedId = tryParseIdThrowExceptionIfUnableToParse(id);

        MonitoredEndpoint monitoredEndpoint = ifEndpointExistReturnItElseThrowException(parsedId);
        if (isTheUserOwnerOfThisEndpoint(accessToken, monitoredEndpoint))
            return logAddNewMonitoringResultAndReturnTheEndpoint(
                    monitoredEndpointService.updateMonitoredEndpoint(parsedId, monitoredEndpointToUpdate)
            );
        else throw logCreateMonitoringResultAndThrowForbiddenException(monitoredEndpoint, "update");
    }

    @DeleteMapping("/{id}")
    void deleteMonitoredEndpoint(@PathVariable String id, @RequestHeader(value = "accessToken") String accessToken) throws InvalidPath, InvalidToken, NoSuchUser {

        checkAccessToken(accessToken);
        int parsedId = tryParseIdThrowExceptionIfUnableToParse(id);

        MonitoredEndpoint endpointToDelete = ifEndpointExistReturnItElseThrowException(parsedId);
        if (isTheUserOwnerOfThisEndpoint(accessToken, endpointToDelete)) {
            logger.info("Deleting Monitored Endpoint id: " + parsedId);
            monitoringResultService.deleteAllMonitoredResultsForMonitoredEndpoint(endpointToDelete);
            monitoredEndpointService
                    .deleteMonitoredEndpoint(monitoredEndpointService.getMonitoredEndpoint(parsedId));
        } else throw logCreateMonitoringResultAndThrowForbiddenException(endpointToDelete, "delete");
    }

    private void checkAccessToken(String accessToken) throws InvalidToken, NoSuchUser {
        if (!accessToken.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")) {
            throw new InvalidToken("Valid token not provided");
        }
        if (userService.getUserByToken(accessToken) == null){
            throw new NoSuchUser("Cannot find user with token: " + accessToken);
        }
    }

    private MonitoredEndpoint ifEndpointExistReturnItElseThrowException(int parsedId) {
        MonitoredEndpoint endpointToDelete = monitoredEndpointService.getMonitoredEndpoint(parsedId);

        if (endpointToDelete == null)
            throw new InvalidPath(String.format("Cannot find endpoint with id: %d", parsedId));
        return endpointToDelete;
    }

    private int tryParseIdThrowExceptionIfUnableToParse(String id) throws InvalidPath {
        int parsedId;

        try {
            parsedId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new InvalidPath(String.format("Cannot find endpoint with id: %s", id));
        }
        return parsedId;
    }

    private MonitoredEndpoint logAddNewMonitoringResultAndReturnTheEndpoint(MonitoredEndpoint getEndpoint) {
        logger.info("Status code: " + HttpStatus.OK.value() + ", payload: " + getEndpoint);
        monitoringResultService.createNewMonitoredResultForMonitoredEndpoint(
                getEndpoint, HttpStatus.OK.value(), getEndpoint.toString()
        );
        return getEndpoint;
    }

    private boolean isTheUserOwnerOfThisEndpoint(String accessToken, MonitoredEndpoint getEndpoint) {
        return getEndpoint.getOwner().equals(userService.getUserByToken(accessToken));
    }

    private ForbiddenException logCreateMonitoringResultAndThrowForbiddenException(MonitoredEndpoint getEndpoint, String message) {
        ForbiddenException unauthorizedException = new ForbiddenException(message);
        logger.info("Status code: " + HttpStatus.FORBIDDEN.value() + ", payload: " + unauthorizedException);
        monitoringResultService.createNewMonitoredResultForMonitoredEndpoint(
                getEndpoint, HttpStatus.FORBIDDEN.value(), unauthorizedException.toString()
        );
        throw unauthorizedException;
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    private static class ForbiddenException extends RuntimeException {
        public ForbiddenException(String message) {
            super(String.format("You are not allowed to %s this endpoint", message));
        }

    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    private static class InvalidPath extends RuntimeException {
        public InvalidPath(String message) {
            super(message);
        }
    }

    @ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
    private static class InvalidToken extends RuntimeException {
        public InvalidToken(String message) {
            super(message);
        }
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    private static class NoSuchUser extends RuntimeException {
        public NoSuchUser(String message) {
            super(message);
        }
    }
}
