package com.example.controller

import com.example.service.ApiLogService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/logs")
class ApiLogController(private val apiLogService: ApiLogService) {

    @GetMapping("/api/{apiName}")
    fun getLogsByApiName(@PathVariable apiName: String): ResponseEntity<Any> {
        val logs = apiLogService.getLogsByApiName(apiName)
        return ResponseEntity.ok(logs)
    }
    
    @GetMapping("/word/{wordId}")
    fun getLogsByWordId(@PathVariable wordId: Long): ResponseEntity<Any> {
        val logs = apiLogService.getLogsByWordId(wordId)
        return ResponseEntity.ok(logs)
    }
    
    @GetMapping("/successful")
    fun getSuccessfulLogs(): ResponseEntity<Any> {
        val logs = apiLogService.getSuccessfulLogs()
        return ResponseEntity.ok(logs)
    }
    
    @GetMapping("/failed")
    fun getFailedLogs(): ResponseEntity<Any> {
        val logs = apiLogService.getFailedLogs()
        return ResponseEntity.ok(logs)
    }
} 