package main.kotlin.com.woong2e.couponsystem.coupon.consumer.listener

import com.fasterxml.jackson.databind.ObjectMapper
import main.kotlin.com.woong2e.couponsystem.coupon.application.service.CouponIssueWorkerService
import main.kotlin.com.woong2e.couponsystem.coupon.consumer.event.CouponIssueEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class CouponIssueConsumer(
    private val couponIssueWorkerService: CouponIssueWorkerService,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        topics = ["coupon-issue-topic"],
        groupId = "coupon-issue-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun couponIssueListener(event: CouponIssueEvent, acknowledgment: Acknowledgment) {
        try {
            log.info("Consumer Listen: couponId={}, userId={}", event.couponId, event.userId)

            couponIssueWorkerService.issue(event.couponId, event.userId)

            acknowledgment.acknowledge()
        } catch (e: Exception) {
            log.error("Consumer Failed: couponId=${event.couponId}, userId=${event.userId}", e)
        }
    }
}