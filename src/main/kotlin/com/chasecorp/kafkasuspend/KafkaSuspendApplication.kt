package com.chasecorp.kafkasuspend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KafkaSuspendApplication

fun main(args: Array<String>) {
    runApplication<KafkaSuspendApplication>(*args)
}
