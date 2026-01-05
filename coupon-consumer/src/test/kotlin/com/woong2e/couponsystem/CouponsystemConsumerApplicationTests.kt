package test.kotlin.com.woong2e.couponsystem

import main.kotlin.com.woong2e.couponsystem.CouponsystemConsumerApplication
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes = [CouponsystemConsumerApplication::class])
@ActiveProfiles("test")
class CouponsystemConsumerApplicationTests {

	@Test
	fun contextLoads() {
	}

}
