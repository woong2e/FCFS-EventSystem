package main.kotlin.com.woong2e.couponsystem.infra.kafka

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.kafka.config.TopicBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaTopicConfig {

    @Bean
    fun couponCreateTopic(): NewTopic {
        return TopicBuilder.name("coupon-issue-topic")
            .partitions(3)
            .replicas(1)
            .build()
    }
}