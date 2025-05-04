package com.example.service

import com.example.entity.Message
import com.example.repository.MessageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import jakarta.annotation.PostConstruct

@Service
class MessageService(private val messageRepository: MessageRepository) {

    @PostConstruct
    fun init() {
        if (messageRepository.count() == 0L) {
            messageRepository.save(Message(content = "Hello MySQL"))
        }
    }

    @Transactional(readOnly = true)
    fun getHelloMessage(): String {
        return messageRepository.findFirstByOrderByIdAsc()?.content ?: "Hello World"
    }
} 