package com.generator

import com.generator.services.GeneratorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class Application @Autowired constructor(private val generatorService: GeneratorService) : CommandLineRunner {
    override fun run(vararg args: String?) {
        generatorService.createDatabase()
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}