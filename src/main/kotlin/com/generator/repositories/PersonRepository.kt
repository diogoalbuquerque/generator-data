package com.generator.repositories

import com.generator.models.Person
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class PersonRepository @Autowired constructor(
    private val jdbcTemplate: JdbcTemplate
) {
    companion object : KLogging()

    fun createTable(database: String) {
        with(jdbcTemplate) {
            execute("DROP TABLE IF EXISTS $database;")
            execute("CREATE TABLE $database (" +
                    "id INT NOT NULL AUTO_INCREMENT, " +
                    "generated_value VARCHAR(100) NOT NULL, " +
                    "old_value VARCHAR(100) NULL, " +
                    " PRIMARY KEY (ID));" +
                    " CREATE UNIQUE INDEX generated_value_unique_$database ON $database(generated_value ASC); " +
                    " CREATE INDEX old_value_unique_$database ON $database(old_value ASC); ")
        }
    }

    fun cleanAllData(database: String) {
        with(jdbcTemplate) {
            execute("TRUNCATE $database;")
        }
    }

    fun countRows(database: String): Int? {
        with(jdbcTemplate) {
            return queryForObject(
                    "SELECT COUNT(*) FROM $database;",
                    Int::class.java)
        }
    }

    fun insertBatchPersonValues(database: String, personGeneratedValues: MutableList<Person>) {

        try {
            with(jdbcTemplate) {
                batchUpdate("INSERT INTO $database(generated_value) VALUES (?)",
                        personGeneratedValues,
                        personGeneratedValues.size) { ps, (generateValue) ->
                    ps.setString(1, generateValue)
                }
            }
        } catch (e: Exception) {
            print(e.message)
        }
    }
}
