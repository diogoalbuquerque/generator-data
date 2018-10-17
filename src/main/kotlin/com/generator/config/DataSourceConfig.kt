package com.generator.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

@Configuration
open class DataSourceConfig @Autowired constructor(private val dsProperties: DSProperties) {

    @Bean
    open fun hikariConfig(): HikariConfig {
        val config = HikariConfig()
        with(config) {
            driverClassName = dsProperties.driverClassName
            jdbcUrl = dsProperties.url
            username = dsProperties.username
            password = dsProperties.password
            maximumPoolSize = dsProperties.maxPoolSize.toInt()
            idleTimeout = dsProperties.idleTimeout.toLong()
            connectionTimeout = dsProperties.connectionTimeout.toLong()
        }

        return config
    }

    @Bean
    open fun hikariDataSource(): HikariDataSource = HikariDataSource(hikariConfig())

    @Bean
    open fun hikariJDBCTemplate(hikariDataSource: DataSource): JdbcTemplate = JdbcTemplate(hikariDataSource)
}