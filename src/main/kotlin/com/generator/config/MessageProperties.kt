package com.generator.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
open class MessageProperties(
    @Value("\${geb.generator.messages.error.environment}")
    val errorEnvironment: String,
    @Value("\${geb.generator.messages.error.idiom}")
    val errorIdiom: String,
    @Value("\${geb.generator.messages.error.filepath}")
    val errorFilepath: String,
    @Value("\${geb.generator.messages.error.external}")
    val errorExternal: String,
    @Value("\${geb.generator.messages.error.server}")
    val errorServer: String,
    @Value("\${geb.generator.messages.success.cleaning}")
    val successCleaning: String,
    @Value("\${geb.generator.messages.success.cleaning-database}")
    val successCleaningDatabase: String,
    @Value("\${geb.generator.messages.success.generate}")
    val successGenerate: String,
    @Value("\${geb.generator.messages.success.generate-database}")
    val successGenerateDatabase: String,
    @Value("\${geb.generator.messages.success.loading}")
    val successLoading: String,
    @Value("\${geb.generator.messages.success.count-rows}")
    val countRows: String,
    @Value("\${geb.generator.messages.info.cleaning-database}")
    val infoCleaningDatabase: String,
    @Value("\${geb.generator.messages.info.generate-database}")
    val infoGenerateDatabase: String,
    @Value("\${geb.generator.messages.info.create}")
    val infoCreate: String,
    @Value("\${geb.generator.messages.info.loading}")
    val infoLoading: String
)
