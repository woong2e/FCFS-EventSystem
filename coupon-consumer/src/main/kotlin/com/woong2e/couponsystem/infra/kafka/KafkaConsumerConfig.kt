package main.kotlin.com.woong2e.couponsystem.infra.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JsonDeserializer

@EnableKafka
@Configuration
class KafkaConsumerConfig(
    private val kafkaProperties: KafkaProperties
) {

    @Bean
    fun consumerFactory(): ConsumerFactory<String, Any> {
        val props = kafkaProperties.buildConsumerProperties()

        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        props[ConsumerConfig.GROUP_ID_CONFIG] = kafkaProperties.consumer.groupId

        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = kafkaProperties.consumer.autoOffsetReset
        props[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = kafkaProperties.consumer.enableAutoCommit

        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = ErrorHandlingDeserializer::class.java
        props[ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS] = JsonDeserializer::class.java

        props[JsonDeserializer.TRUSTED_PACKAGES] = "*"

        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
        factory.consumerFactory = consumerFactory()

        factory.setConcurrency(3)
        factory.containerProperties.ackMode = kafkaProperties.listener.ackMode ?: ContainerProperties.AckMode.RECORD
        return factory
    }
}