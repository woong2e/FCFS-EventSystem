package main.kotlin.com.woong2e.couponsystem.coupon.consumer.listener

import main.kotlin.com.woong2e.couponsystem.coupon.application.port.out.CouponIssueDltPublisher
import main.kotlin.com.woong2e.couponsystem.coupon.application.service.CouponIssueWorkerService
import main.kotlin.com.woong2e.couponsystem.coupon.consumer.event.CouponIssueDltEvent
import main.kotlin.com.woong2e.couponsystem.coupon.consumer.event.CouponIssueEvent
import main.kotlin.com.woong2e.couponsystem.coupon.value.DltSource
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class CouponIssueConsumer(
    private val couponIssueWorkerService: CouponIssueWorkerService,
    private val couponIssueDltPublisher: CouponIssueDltPublisher
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    companion object {
        private const val TOPIC = "coupon-issue-topic"
        private const val TOPIC_DLT = "coupon-issue-dlt-topic"
        private const val GROUP_ID = "coupon-issue-group"
        private const val GROUP_ID_DLT = "coupon-issue-dlt-group"

    }

    @KafkaListener(
        topics = [TOPIC],
        groupId = GROUP_ID,
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun onMessage(event: CouponIssueEvent, ack: Acknowledgment) {
        runCatching {
            log.info("Consumer Listen: couponId={}, userId={}", event.couponId, event.userId)
            couponIssueWorkerService.issue(event.couponId, event.userId)
        }.onSuccess {
            ack.acknowledge()
        }.onFailure { ex ->
            log.error("Consumer Failed -> send to DLT. couponId={}, userId={}", event.couponId, event.userId, ex)

            couponIssueDltPublisher.publish(
                CouponIssueDltEvent(
                    source = DltSource.CONSUMER,
                    couponId = event.couponId,
                    userId = event.userId,
                    reason = ex.message ?: ex::class.java.simpleName
                )
            )
            ack.acknowledge()
        }
    }

    @KafkaListener(
        topics = [TOPIC_DLT],
        groupId = GROUP_ID_DLT,
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun onDltMessage(event: CouponIssueDltEvent, ack: Acknowledgment) {
        log.warn(
            "[DLT][{}] couponId={}, userId={}, reason={}",
            event.source,
            event.couponId,
            event.userId,
            event.reason
        )

        // 일단 그냥 소비해서 토픽에 쌓이지 않게만
        ack.acknowledge()
    }
}