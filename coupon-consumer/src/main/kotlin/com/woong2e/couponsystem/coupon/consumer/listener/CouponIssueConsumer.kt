package main.kotlin.com.woong2e.couponsystem.coupon.consumer.listener

import com.fasterxml.jackson.databind.ObjectMapper
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
    private val couponIssueDltPublisher: CouponIssueDltPublisher,
    private val objectMapper: ObjectMapper // [추가] JSON 파싱용
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
    fun onMessage(messages: List<String>, ack: Acknowledgment) {
        val events = mutableListOf<CouponIssueEvent>()

        messages.forEach { json ->
            try {
                val event = objectMapper.readValue(json, CouponIssueEvent::class.java)
                events.add(event)
            } catch (e: Exception) {
                log.error("Failed to parse event json: {}", json, e)
            }
        }

        if (events.isEmpty()) {
            ack.acknowledge()
            return
        }

        runCatching {
            log.info("Consumer Batch Listen: size={}", events.size)
            couponIssueWorkerService.issueRequestBatch(events)
        }.onSuccess {
            ack.acknowledge()
        }.onFailure { ex ->
            log.error("Batch Consumer Failed -> Send all to DLT. size={}", events.size, ex)

            events.forEach { event ->
                couponIssueDltPublisher.publish(
                    CouponIssueDltEvent(
                        source = DltSource.CONSUMER,
                        couponId = event.couponId,
                        userId = event.userId,
                        reason = ex.message ?: ex::class.java.simpleName
                    )
                )
            }
            ack.acknowledge()
        }
    }

    @KafkaListener(
        topics = [TOPIC_DLT],
        groupId = GROUP_ID_DLT,
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun onDltMessage(message: String, ack: Acknowledgment) {
        try {
            val event = objectMapper.readValue(message, CouponIssueDltEvent::class.java)
            log.warn(
                "[DLT][{}] couponId={}, userId={}, reason={}",
                event.source,
                event.couponId,
                event.userId,
                event.reason
            )
        } catch (e: Exception) {
            log.error("Failed to parse DLT event json: {}", message, e)
        } finally {
            ack.acknowledge()
        }
    }
}