package com.generator.repositories

import com.generator.models.DataAudit
import com.generator.models.Operation
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.query
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DataAuditRepository @Autowired constructor(
    private val jdbcTemplate: JdbcTemplate
) {
    companion object : KLogging()

    fun createTable() {
        with(jdbcTemplate) {
            execute("DROP TABLE IF EXISTS data_audit;")
            execute("CREATE TABLE data_audit (" +
                    "id INT NOT NULL AUTO_INCREMENT, " +
                    "operation VARCHAR(20) NOT NULL, " +
                    "execution_date DATETIME NOT NULL, " +
                    " PRIMARY KEY (ID))")
        }
    }

    fun insertOperationDataAudit(operation: String) {
        with(jdbcTemplate) {
            update("INSERT INTO data_audit(operation, execution_date) VALUES (?,?)", operation, LocalDateTime.now())
        }
    }

    fun selectDataAuditsByOperation(operation: String): List<DataAudit> {
        with(jdbcTemplate) {
            return query(
                    "SELECT id, operation, execution_date  FROM data_audit WHERE operation = ? ", operation) { rs, _ ->
                DataAudit(rs.getLong("id"), Operation.valueOf(rs.getString("operation")), rs.getTimestamp("execution_date").toLocalDateTime())
            }
        }
    }
}
