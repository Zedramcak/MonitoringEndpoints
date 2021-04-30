package eu.adamzrcek.endpointmonitoringapi.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MonitoredEndpointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void noAccessToken_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/monitoredEndpoint"))
                .andExpect(status().is4xxClientError());
    }

}