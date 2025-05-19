package com.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = WorkshopServiceApplication.class) // Specify your main application class
@ActiveProfiles("test")
class WorkshopServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
