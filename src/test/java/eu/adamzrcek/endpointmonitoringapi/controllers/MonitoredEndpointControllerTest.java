package eu.adamzrcek.endpointmonitoringapi.controllers;

import eu.adamzrcek.endpointmonitoringapi.models.MonitoredEndpoint;
import eu.adamzrcek.endpointmonitoringapi.models.User;
import eu.adamzrcek.endpointmonitoringapi.repositories.MonitoredEndpointRepository;
import eu.adamzrcek.endpointmonitoringapi.repositories.UserRepository;
import eu.adamzrcek.endpointmonitoringapi.services.MonitoringResultService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MonitoredEndpointControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MonitoredEndpointRepository endpointRepository;
    @Autowired
    private MonitoringResultService resultService;

    private HttpHeaders headers;

    private String invalidToken;
    private String validToken;
    private String validTokenForUserWithEndpoints;
    private String validTokenForUserWithoutEndpoints;

    private int idOfCreatedEndpoint;

    private User testUserWithEndpoints;
    private User testUserWithoutEndpoints;

    @LocalServerPort
    private int port;

    private String getRootUrl(){
        return "http://localhost:" + port;
    }

    @BeforeAll
    public void setUp() {
        setupTestUser1();
        setupTestUser2();

        userRepository.save(testUserWithEndpoints);
        userRepository.save(testUserWithoutEndpoints);
    }

    private void setupTestUser2() {
        testUserWithoutEndpoints = new User();
        testUserWithoutEndpoints.setUserName("TestUser2");
        testUserWithoutEndpoints.setEmail("noEndpoint@example.com");
        testUserWithoutEndpoints.setAccessToken("bef91a29-5dbb-4a94-9815-36b3f124f459");
    }

    private void setupTestUser1() {
        testUserWithEndpoints = new User();
        testUserWithEndpoints.setUserName("TestUser1");
        testUserWithEndpoints.setEmail("email@example.com");
        testUserWithEndpoints.setAccessToken("af162cd1-8db7-4505-af6e-22babe81085c");
    }

    @BeforeEach
    public void setup(){
        headers = new HttpHeaders();
        invalidToken = "This is not a valid token";
        validToken = "4ffb5e1a-7011-4f5e-9566-a784ff2f131a";
        validTokenForUserWithEndpoints = "af162cd1-8db7-4505-af6e-22babe81085c";
        validTokenForUserWithoutEndpoints = "bef91a29-5dbb-4a94-9815-36b3f124f459";
    }

    @Test
    public void gettingEndpointsWithoutToken_shouldReturn400(){
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/monitoredEndpoint",
                HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void gettingEndpointsWithInvalidToken_shouldReturn412(){
        headers.add("accessToken", invalidToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/monitoredEndpoint",
                HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
    }

    @Test
    public void gettingEndpointsWithValidTokenNotInDatabase_shouldReturn401(){
        headers.add("accessToken", validToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/monitoredEndpoint",
                HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void gettingEndpointWithValidTokenOfUserInDatabase_ShouldReturn200(){
        headers.add("accessToken", validTokenForUserWithEndpoints);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/monitoredEndpoint",
                HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void creatingNewEndpointWithValidToken(){
        headers.add("accessToken", validTokenForUserWithEndpoints);
        MonitoredEndpoint testEndpoint = new MonitoredEndpoint();
        testEndpoint.setName("TEST ENDPOINT");
        HttpEntity<MonitoredEndpoint> entity = new HttpEntity<>(testEndpoint, headers);
        ResponseEntity<MonitoredEndpoint> postResponse = restTemplate.exchange(getRootUrl() + "/monitoredEndpoint",
                HttpMethod.POST , entity, MonitoredEndpoint.class);
        assertNotNull(postResponse);
        assertNotNull(postResponse.getBody());
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        idOfCreatedEndpoint = postResponse.getBody().getId();
    }

    @Test
    public void gettingEndpointWithDifferentUser_ShouldReturn403(){
        headers.add("accessToken", validTokenForUserWithoutEndpoints);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(getRootUrl() + "/monitoredEndpoint/" + idOfCreatedEndpoint,
                HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @AfterAll
    public void tearDown() {
        removeEndpoint();
        deleteTestUsers();
    }

    private void deleteTestUsers() {
        userRepository.delete(testUserWithEndpoints);
        userRepository.delete(testUserWithoutEndpoints);
    }

    @Transactional
    void removeEndpoint() {
        MonitoredEndpoint endpointToDelete = endpointRepository.getMonitoredEndpointById(idOfCreatedEndpoint);
        resultService.deleteAllMonitoredResultsForMonitoredEndpoint(endpointToDelete);
        endpointRepository.delete(endpointToDelete);
    }
}
