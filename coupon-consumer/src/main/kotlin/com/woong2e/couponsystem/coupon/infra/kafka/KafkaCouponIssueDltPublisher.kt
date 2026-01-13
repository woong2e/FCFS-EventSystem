package main.kotlin.com.woong2e.couponsystem.coupon.infra.kafka

import main.kotlin.com.woong2e.couponsystem.coupon.application.port.out.CouponIssueDltPublisher
import main.kotlin.com.woong2e.couponsystem.coupon.consumer.event.CouponIssueDltEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaCouponIssueDltPublisher(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
) : CouponIssueDltPublisher {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val TOPIC_DLT = "coupon-issue-dlt-topic"
    }

    override fun publish(event: CouponIssueDltEvent) {
        val key = event.userId.toString()

        kafkaTemplate.send(TOPIC_DLT, key, event).whenComplete { _, ex ->
            if (ex != null) {
                log.error("Failed to send to DLT. couponId={}, userId={}", event.couponId, event.userId, ex)
            }
        }
    }
}