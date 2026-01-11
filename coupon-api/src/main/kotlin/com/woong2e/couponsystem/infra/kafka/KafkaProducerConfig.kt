package main.kotlin.com.woong2e.couponsystem.infra.kafka

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
class KafkaProducerConfig(
    private val kafkaProperties: KafkaProperties
) {

    @Bean
    fun producerFactory(): ProducerFactory<String, Any> {
        val props = HashMap<String, Any>()

        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers


        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java

        props[ProducerConfig.ACKS_CONFIG] = kafkaProperties.producer.acks
        props[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = kafkaProperties.producer.properties["enable.idempotence"] ?: true

        props[ProducerConfig.LINGER_MS_CONFIG] = 5
        props[ProducerConfig.BATCH_SIZE_CONFIG] = kafkaProperties.producer.batchSize

        props[ProducerConfig.COMPRESSION_TYPE_CONFIG] = kafkaProperties.producer.compressionType

        return DefaultKafkaProducerFactory(props)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, Any> {
        return KafkaTemplate(producerFactory())
    }
}