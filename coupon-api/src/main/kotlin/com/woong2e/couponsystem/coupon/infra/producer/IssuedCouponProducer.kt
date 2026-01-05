package main.kotlin.com.woong2e.couponsystem.coupon.infra.producer

import main.kotlin.com.woong2e.couponsystem.coupon.application.event.CouponIssueEvent
import main.kotlin.com.woong2e.couponsystem.coupon.application.port.out.CouponIssueEventPublisher
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class IssuedCouponProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) : CouponIssueEventPublisher {

    private val log = LoggerFactory.getLogger(this::class.java)

    companion object {
        private const val TOPIC = "coupon-issue-topic"
    }

    override fun publish(couponId: UUID, userId: Long) {
        val event = CouponIssueEvent(couponId, userId)

        val future = kafkaTemplate.send(TOPIC, userId.toString(), event)

        future.whenComplete { result, ex ->
            if (ex == null) {
                log.info(
                    "Success send message: topic={}, partition={}, offset={}, couponId={}",
                    result.recordMetadata.topic(),
                    result.recordMetadata.partition(),
                    result.recordMetadata.offset(),
                    couponId
                )
            } else {
                log.error("Failed to send message: couponId=$couponId, userId=$userId", ex)
            }
        }
    }
}