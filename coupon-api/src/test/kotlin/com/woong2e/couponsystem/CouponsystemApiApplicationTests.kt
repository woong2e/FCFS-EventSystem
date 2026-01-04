package test.kotlin.com.woong2e.couponsystem

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes = [CouponsystemApiApplicationTests::class])
@ActiveProfiles("test")
class CouponsystemApiApplicationTests {

	@Test
	fun contextLoads() {
	}

}
