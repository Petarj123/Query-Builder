package com.petar.querybuilder.impl

import com.petar.querybuilder.client.ConnectionClient
import com.petar.querybuilder.core.BaseQueryBuilder
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.SQLException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class InsertQueryBuilder(private val table: String, private val connectionClient: ConnectionClient) : BaseQueryBuilder<InsertQueryBuilder>() {

    override val jdbcTemplate: JdbcTemplate = connectionClient.jdbcTemplate
    override val queryType: QueryType = QueryType.INSERT
    private val values = mutableListOf<String>()

    override fun build(): String {
        val columnsWithTypes = getTableColumnsWithTypes(table)

        if (values.isEmpty()) throw IllegalStateException("Values cannot be empty for an INSERT query")

        // Validating column types with provided values
        columns.forEachIndexed { index, columnName ->
            val columnType = columnsWithTypes[columnName] ?: throw IllegalArgumentException("Column $columnName does not exist in table $table")

            validateValue(values[index], columnType)
        }

        val selectedColumns = if (columns.isEmpty()) "" else columns.joinToString(", ", prefix = "(", postfix = ")")
        val formattedValues = formatValues(values, columnsWithTypes)

        return "INSERT INTO $table $selectedColumns VALUES ($formattedValues)"
    }
    fun values(vararg args: String): BaseQueryBuilder<InsertQueryBuilder> {
        values.addAll(args)
        return this
    }

    private fun getTableColumnsWithTypes(tableName: String): Map<String, String> {
        val columns = mutableMapOf<String, String>()

        val connection = connectionClient.getConnection()

        // Autocloseable, connection is automatically closed, replaces try/catch
        connection.use { conn ->
            val metadata = conn.metaData ?: throw SQLException("Cannot get metadata")
            val resultSet = metadata.getColumns(null, null, tableName, null)

            while (resultSet.next()) {
                val columnName = resultSet.getString("COLUMN_NAME")
                val columnType = resultSet.getString("TYPE_NAME")
                columns[columnName] = columnType
            }
        }

        return columns
    }
    private fun formatValues(values: List<String>, columnsWithTypes: Map<String, String>): String {
        return values.mapIndexed { index, value ->
            val columnType = columnsWithTypes[columns[index]] ?: throw IllegalStateException("Column type for ${columns[index]} not found.")
            when (columnType) {
                "VARCHAR", "TEXT" -> "'$value'"
                "INTEGER", "BIGINT", "DECIMAL" -> value // assume that validation is done, and value is a number
                else -> value //TODO add support for other types
            }
        }.joinToString(", ")
    }
    private fun validateValue(value: String, type: String) {
        when(type.uppercase()) {
            "VARCHAR", "TEXT", "CHAR" -> {
                // For string types, you might want to check the length, or perform other string-specific validations.
            }
            "INTEGER", "SMALLINT", "BIGINT", "INT8" -> {
                try {
                    value.toLong() // Try to convert the value to Long, will throw an exception if it's not a valid integer
                } catch (e: NumberFormatException) {
                    throw IllegalArgumentException("Value $value is not a valid integer for type $type")
                }
            }
            "DECIMAL", "FLOAT", "REAL", "DOUBLE" -> {
                try {
                    value.toDouble() // Try to convert the value to Double, will throw an exception if it's not a valid decimal number
                } catch (e: NumberFormatException) {
                    throw IllegalArgumentException("Value $value is not a valid decimal number for type $type")
                }
            }
            "DATE" -> {
                try {
                    // Assuming date is represented in 'YYYY-MM-DD' format
                    LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE)
                } catch (e: DateTimeParseException) {
                    throw IllegalArgumentException("Value $value is not a valid date for type $type")
                }
            }
            "TIMESTAMP" -> {
                try {
                    // Assuming timestamp is represented in 'YYYY-MM-DDTHH:mm:ss' format
                    LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                } catch (e: DateTimeParseException) {
                    throw IllegalArgumentException("Value $value is not a valid timestamp for type $type")
                }
            }
            else -> {
                //TODO Add support for more types or throw an exception for unsupported types.
                throw UnsupportedOperationException("Type $type is not supported")
            }
        }
    }
    private fun getPrimaryKeyColumnName(tableName: String): String {
        val connection = connectionClient.getConnection()
        connection.use { conn ->
            val metadata = conn.metaData ?: throw SQLException("Cannot get metadata")
            val primaryKeysRs = metadata.getPrimaryKeys(null, null, tableName)

            if (primaryKeysRs.next()) {
                return primaryKeysRs.getString("COLUMN_NAME")
            } else {
                throw SQLException("No primary key found for table $tableName")
            }
        }
    }

}