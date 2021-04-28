package eu.adamzrcek.endpointmonitoringapi.configuration;

import eu.adamzrcek.endpointmonitoringapi.models.User;
import eu.adamzrcek.endpointmonitoringapi.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class fillUserDatabase {

    Logger logger = LoggerFactory.getLogger(fillUserDatabase.class);

    @Bean
    CommandLineRunner addUsersToDatabaseIfEmpty(UserService userService){
        if (userService.findAll().size() == 0){
            User applifting = new User();
            User batman = new User();
            applifting.setUserName("Applifting");
            applifting.setEmail("info@applifting.cz");
            applifting.setAccessToken("93f39e2f-80de-4033-99ee-249d92736a25");
            batman.setUserName("Batman");
            batman.setEmail("batman@example.com");
            batman.setAccessToken("dcb20f8a-5657-4f1b-9f7f-ce65739b359e");

            userService.addNewUser(applifting);
            userService.addNewUser(batman);
            logger.debug("Added new Users to empty database");
        }
        return null;
    }
}
