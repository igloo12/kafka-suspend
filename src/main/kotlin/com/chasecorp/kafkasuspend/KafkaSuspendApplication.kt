package com.chasecorp.kafkasuspend

import io.ktor.client.plugins.*
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.stereotype.Component
import org.springframework.util.backoff.FixedBackOff

@SpringBootApplication
class KafkaSuspendApplication

fun main(args: Array<String>) {
    runApplication<KafkaSuspendApplication>(*args)
}


@Component
class KafkaListeners(template: KafkaTemplate<String, String>) {
    init {
        template.send("test", "test")
    }

    // DOESN'T WORK
    @KafkaListener(
        id = "nocall",
        groupId = "groupid",
        topics = ["test"],
    )
    suspend fun doesntCall(message: String) {
        throw HttpRequestTimeoutException("google.com", 100)
    }

    // WORKS
    @KafkaListener(
        id = "calls",
        groupId = "groupid",
        topics = ["test"],
    )
    fun calls(message: String) {
        throw HttpRequestTimeoutException("google.com", 100)
    }
}

@Configuration
class ErrorConfig {
    val logger = LoggerFactory.getLogger(ErrorConfig::class.java)

    @Bean
    fun defaultErrorHandler(): DefaultErrorHandler {
        val defaultErrorHandler = DefaultErrorHandler()
        defaultErrorHandler.setBackOffFunction { _, exception ->
            logger.info("call me maybe")
            FixedBackOff(2000, 100)
        }
        return defaultErrorHandler
    }
}