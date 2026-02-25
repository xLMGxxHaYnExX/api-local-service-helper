package io.devhub.apilocalservicehelper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Application Context Tests
 * Verifies that the Spring Boot application context loads successfully
 * with all beans properly initialized.
 */
@SpringBootTest
@ActiveProfiles("test")
class DevCommandHubApplicationTests {

	@Test
	@DisplayName("Application context loads successfully")
	void contextLoads() {
		// If this test passes, the application context has loaded successfully
		// All beans and configurations are properly initialized
		assertNotNull(this);
	}
}


