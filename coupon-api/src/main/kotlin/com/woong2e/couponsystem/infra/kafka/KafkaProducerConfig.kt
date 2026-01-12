package main.kotlin.com.woong2e.couponsystem.infra.kafka

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
class KafkaProducerConfig(
    private val kafkaProperties: KafkaProperties
) {

    companion object {
        private const val DEFAULT_LINGER_MS = 50
        private const val DEFAULT_BATCH_SIZE = 1000000
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        val props = HashMap<String, Any>()

        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers

        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java

        props[ProducerConfig.ACKS_CONFIG] = kafkaProperties.producer.acks
        props[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = kafkaProperties.producer.properties["enable.idempotence"] ?: true

        props[ProducerConfig.LINGER_MS_CONFIG] = DEFAULT_LINGER_MS
        props[ProducerConfig.BATCH_SIZE_CONFIG] = DEFAULT_BATCH_SIZE

        props[ProducerConfig.COMPRESSION_TYPE_CONFIG] = kafkaProperties.producer.compressionType

        return DefaultKafkaProducerFactory(props)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory())
    }
}