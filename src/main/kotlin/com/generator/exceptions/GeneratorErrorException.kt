package com.generator.exceptions

import mu.KLogging

class GeneratorErrorException(message: String?) : Exception(message) {
    companion object : KLogging()

    init {
        logger.error { message }
    }
}