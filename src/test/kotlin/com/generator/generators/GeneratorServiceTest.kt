package com.generator.generators

import com.generator.config.DirectoryProperties
import com.generator.models.DatabaseStatus
import com.generator.models.Operation
import com.generator.services.GeneratorService
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.test.context.junit4.SpringRunner
import java.text.MessageFormat.format

@RunWith(SpringRunner::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class GeneratorServiceTest : GeneralUtilTest() {

    @Test
    fun should__throw_server_error_when_clean_matrix() {
        assertThatThrownBy {
            generatorService.removeDataMatrixNameCPF()
        }.hasMessageContaining(messageProperties.errorServer)
    }

    @Test
    fun should__throw_idiom_error_when_generate_matrix() {
        val invalidIdiom = "invalid_idiom"
        assertThatThrownBy {
            generatorService.generateDataMatrixNameCPF(invalidIdiom)
        }.hasMessage(format(messageProperties.errorIdiom, invalidIdiom))
    }

    @Test
    fun should__throw_environment_error_when_generate_matrix() {
        assertThatThrownBy {
            generatorService.generateDataMatrixNameCPF("")
        }.hasMessageContaining(messageProperties.errorEnvironment)
    }

    @Test
    fun should__throw_file_path_error_when_generate_matrix() {
        assertThatThrownBy {
            generatorService.loadFileFromResource(IDIOM_DEFAULT_TEST)
        }.hasMessageContaining(messageProperties.errorFilepath.split(":")[0])
    }

    @Test
    fun should__throw_external_path_error_when_generate_matrix() {
        val invalidPath = "invalid_path"
        generatorService = GeneratorService(cpfGenerator, DirectoryProperties(invalidPath), messageProperties,
                personRepository, dataAuditRepository)
        assertThatThrownBy {
            generatorService.generateDataMatrixNameCPF(IDIOM_DEFAULT_TEST)
        }.hasMessageContaining(messageProperties.errorExternal.split(":")[0])
    }

    @Test
    fun should_create_database() {
        generatorService.createDatabase()
    }

    @Test
    fun should_create_database_and_status_up() {
        runBlocking {
            generatorService.createDatabase()
            delay(DELAY_TIME_MILLIS)
        }
        assertThat(generatorService.statusDatabase().databaseStatus).isEqualTo(DatabaseStatus.UP.value)
    }

    @Test
    fun should_create_database_and_clean_all_registers() {
        runBlocking {
            generatorService.createDatabase()
            delay(DELAY_TIME_MILLIS)
        }
        assertThat(generatorService.statusDatabase().databaseStatus).isEqualTo(DatabaseStatus.UP.value)
        generatorService.removeDataMatrixNameCPF()
        assertThat(dataAuditRepository.selectDataAuditsByOperation(Operation.DELETE.value)).isNotEmpty
    }

    @Test
    fun should_create_database_and_generate_matrix() {
        runBlocking {
            generatorService.createDatabase()
            delay(DELAY_TIME_MILLIS)
        }
        assertThat(generatorService.statusDatabase().databaseStatus).isEqualTo(DatabaseStatus.UP.value)
        generatorService.generateDataMatrixNameCPF(IDIOM_DEFAULT_TEST)
    }
}