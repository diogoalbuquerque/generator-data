package com.generator.generators

import com.generator.config.DSProperties
import com.generator.config.DataSourceConfig
import com.generator.config.DirectoryProperties
import com.generator.config.MessageProperties
import com.generator.controllers.GeneratorController
import com.generator.repositories.DataAuditRepository
import com.generator.repositories.PersonRepository
import com.generator.services.GeneratorService
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest(classes = [
    (CPFGenerator::class),
    (DirectoryProperties::class),
    (MessageProperties::class),
    (GeneratorController::class),
    (GeneratorService::class),
    (PersonRepository::class),
    (DataSourceConfig::class),
    (DSProperties::class),
    (DataAuditRepository::class)
])
abstract class GeneralUtilTest {
    companion object {
        const val DELAY_TIME_MILLIS: Int = 10000
        const val IDIOM_DEFAULT_TEST: String = "ptbr"
    }

    @Autowired
    protected
    lateinit var generatorController: GeneratorController

    @Autowired
    protected
    lateinit var messageProperties: MessageProperties

    @Autowired
    protected
    lateinit var generatorService: GeneratorService

    @Autowired
    protected
    lateinit var personRepository: PersonRepository

    @Autowired
    protected
    lateinit var dataAuditRepository: DataAuditRepository

    @Autowired
    private
    lateinit var jdbcTemplate: JdbcTemplate

    private lateinit var directoryProperties: DirectoryProperties

    protected lateinit var cpfGenerator: CPFGenerator

    @Before
    open fun setup() {
        directoryProperties = DirectoryProperties("")
        cpfGenerator = CPFGenerator()
        personRepository = PersonRepository(jdbcTemplate)
        dataAuditRepository = DataAuditRepository(jdbcTemplate)
        generatorService = GeneratorService(cpfGenerator, directoryProperties, messageProperties,
                personRepository, dataAuditRepository)
        generatorController = GeneratorController(generatorService)
    }
}