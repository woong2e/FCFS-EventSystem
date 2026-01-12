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

    /**
     * [Batch Listener 적용]
     * ack-mode: batch, listener.type: batch 설정 필요
     * 메시지를 List 형태로 묶어서 수신 -> Bulk Insert로 처리량 극대화
     */
    @KafkaListener(
        topics = [TOPIC],
        groupId = GROUP_ID,
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun onMessage(events: List<CouponIssueEvent>, ack: Acknowledgment) {
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

    /**
     * DLT 리스너는 처리가 급하지 않으므로 단건 처리 유지, 일단 소모하도록
     */
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

        ack.acknowledge()
    }
}