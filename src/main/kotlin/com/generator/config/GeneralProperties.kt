package com.generator.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
open class DirectoryProperties(
    @Value("\${geb.generator.path.input}")
    val inputDirectoryPath: String
)

@Configuration
open class DSProperties(
    @Value("\${geb.generator.datasource.url}") val url: String,
    @Value("\${geb.generator.datasource.username}") val username: String,
    @Value("\${geb.generator.datasource.password}") val password: String,
    @Value("\${geb.generator.datasource.driver-class-name}") val driverClassName: String,
    @Value("\${geb.generator.datasource.maximum-pool-size}") val maxPoolSize: String,
    @Value("\${geb.generator.datasource.idle-timeout}") val idleTimeout: String,
    @Value("\${geb.generator.datasource.connection-timeout}") val connectionTimeout: String
)