package com.generator.models

import java.time.LocalDateTime

data class DataAudit(
    val id: Long,
    val operation: Operation,
    val execution_date: LocalDateTime
)