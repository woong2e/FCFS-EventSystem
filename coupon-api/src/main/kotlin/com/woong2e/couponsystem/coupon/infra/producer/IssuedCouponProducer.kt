package main.kotlin.com.woong2e.couponsystem.coupon.infra.producer

import main.kotlin.com.woong2e.couponsystem.coupon.application.event.CouponIssueDltEvent
import main.kotlin.com.woong2e.couponsystem.coupon.application.event.CouponIssueEvent
import main.kotlin.com.woong2e.couponsystem.coupon.application.port.out.CouponIssueEventPublisher
import main.kotlin.com.woong2e.couponsystem.coupon.value.DltSource
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
        private const val TOPIC_DLT = "coupon-issue-dlt-topic"
    }

    override fun publish(couponId: UUID, userId: Long) {
        val event = CouponIssueEvent(couponId, userId)
        val key = userId.toString()

        runCatching { kafkaTemplate.send(TOPIC, key, event) }
            .onFailure { ex ->
                log.error("Kafka send() call failed -> send to DLT. couponId={}, userId={}", couponId, userId, ex)
                sendToDlt(
                    key = key,
                    dltEvent = CouponIssueDltEvent(
                        source = DltSource.PRODUCER,
                        couponId = couponId,
                        userId = userId,
                        reason = ex.message ?: ex::class.java.simpleName
                    )
                )
            }
            .onSuccess { future ->
                future.whenComplete { result, ex ->
                    if (ex == null) {
                        log.info(
                            "Producer sent: topic={}, partition={}, offset={}, couponId={}, userId={}",
                            result.recordMetadata.topic(),
                            result.recordMetadata.partition(),
                            result.recordMetadata.offset(),
                            couponId,
                            userId
                        )
                        return@whenComplete
                    }

                    log.error("Producer async send failed -> send to DLT. couponId={}, userId={}", couponId, userId, ex)
                    sendToDlt(
                        key = key,
                        dltEvent = CouponIssueDltEvent(
                            source = DltSource.PRODUCER,
                            couponId = couponId,
                            userId = userId,
                            reason = ex.message ?: ex::class.java.simpleName
                        )
                    )
                }
            }
    }

    private fun sendToDlt(key: String, dltEvent: CouponIssueDltEvent) {
        kafkaTemplate.send(TOPIC_DLT, key, dltEvent).whenComplete { result, ex ->
            if (ex == null) {
                log.warn(
                    "Sent to DLT: topic={}, partition={}, offset={}, source={}, couponId={}, userId={}",
                    result.recordMetadata.topic(),
                    result.recordMetadata.partition(),
                    result.recordMetadata.offset(),
                    dltEvent.source,
                    dltEvent.couponId,
                    dltEvent.userId
                )
            } else {
                log.error("Failed to send to DLT: source={}, couponId={}, userId={}", dltEvent.source, dltEvent.couponId, dltEvent.userId, ex)
            }
        }
    }
}