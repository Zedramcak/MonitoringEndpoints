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
import java.awt.*;
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
    List<MonitoredEndpoint> getAllEndpoints(@RequestHeader(value = "accessToken") String accessToken) throws InvalidPrecondition, NoSuchUser {
        checkAccessToken(accessToken, null);

        return monitoredEndpointService.getMonitoredEndpointsForUser(userService.getUserByToken(accessToken));
    }

    @PostMapping("")
    MonitoredEndpoint addNewEndpoint(@RequestHeader(value = "accessToken") String accessToken,
                                     @RequestBody MonitoredEndpoint newMonitoredEndpoint,
                                     HttpServletResponse response) throws InvalidPrecondition, NoSuchUser {

        checkAccessToken(accessToken, null);
        checkHTTPBodyParameters(newMonitoredEndpoint, null);

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
                                           @RequestHeader(value = "accessToken") String accessToken) throws InvalidPath, InvalidPrecondition, NoSuchUser {
        MonitoredEndpoint getEndpoint = returnMonitoredEndpointIfItExistsElseThrowException(tryParseIdThrowExceptionIfUnableToParse(id));
        checkAccessToken(accessToken, getEndpoint);

        if (isTheUserOwnerOfThisEndpoint(accessToken, getEndpoint)) {
            return logAddNewMonitoringResultAndReturnTheEndpoint(getEndpoint);
        } else throw logCreateMonitoringResultAndThrowForbiddenException(getEndpoint, "read");
    }

    @GetMapping("/{id}/monitoringResults")
    List<MonitoringResult> getMonitoringResultsForMonitoredEndpoint(@PathVariable String id,
                                                                    @RequestHeader(value = "accessToken") String accessToken) throws InvalidPath, InvalidPrecondition, NoSuchUser {
        MonitoredEndpoint getEndpoint = returnMonitoredEndpointIfItExistsElseThrowException(tryParseIdThrowExceptionIfUnableToParse(id));
        checkAccessToken(accessToken, getEndpoint);

        if (isTheUserOwnerOfThisEndpoint(accessToken, getEndpoint))
            return monitoringResultService.getLastTenMonitoredResultsForMonitoredEndpoint(getEndpoint);
        else throw logCreateMonitoringResultAndThrowForbiddenException(getEndpoint, "read");
    }


    @PutMapping("/{id}")
    MonitoredEndpoint updateMonitoredEndpoint(@RequestHeader(value = "accessToken") String accessToken,
                                              @PathVariable String id,
                                              @RequestBody MonitoredEndpoint monitoredEndpointToUpdate) throws InvalidPath, InvalidPrecondition, NoSuchUser {
        MonitoredEndpoint getEndpoint = returnMonitoredEndpointIfItExistsElseThrowException(tryParseIdThrowExceptionIfUnableToParse(id));
        checkAccessToken(accessToken, getEndpoint);

        checkHTTPBodyParameters(monitoredEndpointToUpdate, getEndpoint);

        if (isTheUserOwnerOfThisEndpoint(accessToken, getEndpoint))
            return logAddNewMonitoringResultAndReturnTheEndpoint(
                    monitoredEndpointService.updateMonitoredEndpoint(getEndpoint.getId(), monitoredEndpointToUpdate)
            );
        else throw logCreateMonitoringResultAndThrowForbiddenException(getEndpoint, "update");
    }


    @DeleteMapping("/{id}")
    void deleteMonitoredEndpoint(@PathVariable String id, @RequestHeader(value = "accessToken") String accessToken) throws InvalidPath, InvalidPrecondition, NoSuchUser {

        MonitoredEndpoint endpointToDelete = returnMonitoredEndpointIfItExistsElseThrowException(tryParseIdThrowExceptionIfUnableToParse(id));
        checkAccessToken(accessToken, endpointToDelete);

        if (isTheUserOwnerOfThisEndpoint(accessToken, endpointToDelete)) {
            logger.info("Deleting Monitored Endpoint id: " + endpointToDelete.getId());
            monitoringResultService.deleteAllMonitoredResultsForMonitoredEndpoint(endpointToDelete);
            monitoredEndpointService
                    .deleteMonitoredEndpoint(monitoredEndpointService.getMonitoredEndpoint(endpointToDelete.getId()));
        } else throw logCreateMonitoringResultAndThrowForbiddenException(endpointToDelete, "delete");
    }

    private void checkHTTPBodyParameters(MonitoredEndpoint monitoredEndpointFromHTTPBody, MonitoredEndpoint existingEndpoint) {
        String message;
        if (monitoredEndpointFromHTTPBody.getName() == null || monitoredEndpointFromHTTPBody.getUrl() == null) {
            message = "HTTP body is missing either name or url";
            logger.info("Status code: " + HttpStatus.PRECONDITION_FAILED.value() + ", payload: " + message);

            if (existingEndpoint!=null) monitoringResultService.createNewMonitoredResultForMonitoredEndpoint(existingEndpoint, HttpStatus.PRECONDITION_FAILED.value(), message);

            throw new InvalidPrecondition(message);
        }
        if (!monitoredEndpointFromHTTPBody.getUrl().matches("https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)")){
            message = "Wrong url format";
            logger.info("Status code: " + HttpStatus.PRECONDITION_FAILED.value() + ", payload: " + message);

            if (existingEndpoint!=null) monitoringResultService.createNewMonitoredResultForMonitoredEndpoint(existingEndpoint, HttpStatus.PRECONDITION_FAILED.value(), message);

            throw new InvalidPrecondition(message);
        }
    }

    private void checkAccessToken(String accessToken, MonitoredEndpoint monitoredEndpoint) throws InvalidPrecondition, NoSuchUser {
        String message;
        if (!accessToken.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")) {
            message = "Valid token not provided";
            logger.info("Status code: " + HttpStatus.PRECONDITION_FAILED.value() + ", payload: " + message);
            if (monitoredEndpoint!=null) {
                monitoringResultService.createNewMonitoredResultForMonitoredEndpoint(monitoredEndpoint, HttpStatus.PRECONDITION_FAILED.value(), message);
            }
            throw new InvalidPrecondition(message);
        }
        if (userService.getUserByToken(accessToken) == null){
            message = "Cannot find user with token: " + accessToken;
            logger.info("Status code: " + HttpStatus.UNAUTHORIZED.value() + ", payload: " + message);
            if (monitoredEndpoint!=null) {
                monitoringResultService.createNewMonitoredResultForMonitoredEndpoint(monitoredEndpoint, HttpStatus.UNAUTHORIZED.value(), message);
            }
            throw new NoSuchUser("Cannot find user with token: " + accessToken);
        }
    }

    private MonitoredEndpoint returnMonitoredEndpointIfItExistsElseThrowException(int parsedId) {
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
        String forbiddenMessage = String.format("You are not allowed to %s this endpoint", message); //Great Cthulhu will be summoned by these words
        logger.info("Status code: " + HttpStatus.FORBIDDEN.value() + ", payload: " + forbiddenMessage);
        monitoringResultService.createNewMonitoredResultForMonitoredEndpoint(
                getEndpoint, HttpStatus.FORBIDDEN.value(), forbiddenMessage
        );
        throw new ForbiddenException(forbiddenMessage); // Great Cthulhu is coming
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    private static class ForbiddenException extends RuntimeException {
        public ForbiddenException(String message) {
            super(message);
        }

    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    private static class InvalidPath extends RuntimeException {
        public InvalidPath(String message) {
            super(message);
        }
    }

    @ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
    private static class InvalidPrecondition extends RuntimeException {
        public InvalidPrecondition(String message) {
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
