package com.example

import com.example.service.MessageService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}

@RestController
class HelloController(private val messageService: MessageService) {
    @GetMapping("/api/hello")
    fun hello(): String = messageService.getHelloMessage()
} 