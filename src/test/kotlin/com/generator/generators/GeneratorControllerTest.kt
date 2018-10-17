package com.generator.generators

import com.generator.models.DatabaseStatus
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.text.MessageFormat.format

@RunWith(SpringRunner::class)
class GeneratorControllerTest : GeneralUtilTest() {

    private lateinit var mvc: MockMvc

    @Before
    override fun setup() {
    mvc = MockMvcBuilders.standaloneSetup(generatorController).build()
    }

    @Test
    fun should_create_database_and_status_up() {
        runBlocking {
            generatorService.createDatabase()
            delay(DELAY_TIME_MILLIS)
        }

        mvc.perform(get("/generator/status/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.databaseStatus", equalTo(DatabaseStatus.UP.value)))
    }

    @Test
    fun should_create_database_and_clean() {
        runBlocking {
            generatorService.createDatabase()
            delay(DELAY_TIME_MILLIS)
        }

        mvc.perform(delete("/generator/clean/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().string(messageProperties.successCleaning))
    }

    @Test
    fun should_create_database_and_generate_matrix() {
        runBlocking {
            generatorService.createDatabase()
            delay(DELAY_TIME_MILLIS)
        }

        mvc.perform(post("/generator/create/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().string(messageProperties.successGenerate))
    }

    @Test
    fun should_create_database_and_thrown_internal_server_error_when_generate_matrix() {
        val invalidIdiom = "invalid_idiom"
        runBlocking {
            generatorService.createDatabase()
            delay(DELAY_TIME_MILLIS)
        }

        mvc.perform(post("/generator/create/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"idiom\":\"$invalidIdiom\" }"))
                .andExpect(status().isInternalServerError)
                .andExpect(content().string(format(messageProperties.errorIdiom, invalidIdiom)))
    }

    @Test
    fun should_create_database_and_generate_matrix_and_clean_data() {
        val validIdiom = "ptbr"
        runBlocking {
            generatorService.createDatabase()
            delay(DELAY_TIME_MILLIS)
        }

        mvc.perform(get("/generator/status/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.databaseStatus", equalTo(DatabaseStatus.UP.value)))

        mvc.perform(post("/generator/create/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"idiom\":\"$validIdiom\" }"))
                .andExpect(status().isOk)
                .andExpect(content().string(messageProperties.successGenerate))

        runBlocking {
            mvc.perform(get("/generator/status/")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.databaseStatus", equalTo(DatabaseStatus.UP.value)))
            delay(DELAY_TIME_MILLIS)
        }

        mvc.perform(delete("/generator/clean/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().string(messageProperties.successCleaning))

        mvc.perform(get("/generator/status/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.databaseStatus", equalTo(DatabaseStatus.UP.value)))
    }
}