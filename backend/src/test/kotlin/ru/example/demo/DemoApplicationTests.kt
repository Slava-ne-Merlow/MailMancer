package ru.example.demo

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles


@ActiveProfiles("test")
@SpringBootTest
class DemoApplicationTests {

	@Test
	fun contextLoads() {
		// Empty test to verify context loads successfully
	}

}
