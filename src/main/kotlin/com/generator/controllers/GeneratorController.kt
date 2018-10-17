package com.generator.controllers

import com.generator.generators.GeneratorConstants.Companion.LANGUAGE_PT_BT
import com.generator.models.DatabaseStatus
import com.generator.models.LanguageTO
import com.generator.models.StatusTO
import com.generator.services.GeneratorService
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.GetMapping

@RestController
@RequestMapping("/generator")
class GeneratorController @Autowired constructor(
    private val generatorService: GeneratorService
) {
    companion object : KLogging()

    @PostMapping("/create")
    fun generateDatabase(@RequestBody languageTO: LanguageTO?): ResponseEntity<String?> {
        return try {
            ResponseEntity(generatorService.generateDataMatrixNameCPF(languageTO?.idiom ?: LANGUAGE_PT_BT),
                    HttpStatus.OK)
        } catch (e: Exception) {
            logger.error { e.message }
            ResponseEntity(e.message,
                    HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @DeleteMapping("/clean")
    fun dropDatabase(): ResponseEntity<String?> {
        return try {
            ResponseEntity(generatorService.removeDataMatrixNameCPF(),
                    HttpStatus.OK)
        } catch (e: Exception) {
            logger.error { e.message }
            ResponseEntity(e.message,
                    HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/status")
    fun statusDatabase(): ResponseEntity<StatusTO> {
        return try {
            ResponseEntity(generatorService.statusDatabase(),
                    HttpStatus.OK)
        } catch (e: Exception) {
            logger.error { e.message }
            ResponseEntity(StatusTO(DatabaseStatus.DOWN.value),
                    HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}