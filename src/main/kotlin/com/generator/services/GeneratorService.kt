package com.generator.services

import com.generator.config.DirectoryProperties
import com.generator.config.MessageProperties
import com.generator.exceptions.GeneratorErrorException
import com.generator.generators.CPFGenerator
import com.generator.generators.GeneratorConstants.Companion.FIRST_NAME_FILE
import com.generator.generators.GeneratorConstants.Companion.LAST_NAME_FILE
import com.generator.generators.GeneratorConstants.Companion.RESOURCE_DIRECTORY
import com.generator.models.DatabaseTable
import com.generator.models.DatabaseStatus
import com.generator.models.Operation
import com.generator.models.StatusTO
import com.generator.models.Person
import com.generator.repositories.DataAuditRepository
import com.generator.repositories.PersonRepository
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.awaitAll
import kotlinx.coroutines.experimental.runBlocking
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.text.MessageFormat

@Service
open class GeneratorService @Autowired constructor(
    private val cpfGenerator: CPFGenerator,
    private val directoryProperties: DirectoryProperties,
    private val messageProperties: MessageProperties,
    private val personRepository: PersonRepository,
    private val dataAuditRepository: DataAuditRepository
) {

    companion object : KLogging()

    @Transactional
    open fun createDatabase() = runBlocking {
        async {
            dataAuditRepository.createTable()
        }
        DatabaseTable.values().forEach { dbName ->
            async {
                try {
                    personRepository.createTable(dbName.value)
                } catch (e: Exception) {
                    throw GeneratorErrorException(e.message)
                }
            }
        }
    }

    @Transactional
    open fun removeDataMatrixNameCPF(): String {

        try {
            dataAuditRepository.insertOperationDataAudit(Operation.DELETE.value)
            async {
                cleanAllData()
            }
            return messageProperties.successCleaning
        } catch (e: Exception) {
            throw GeneratorErrorException(messageProperties.errorServer)
        }
    }

    @Transactional
    open fun statusDatabase(): StatusTO {

        try {
            val databaseRows = mutableListOf<String>()
            DatabaseTable.values().forEach { dbName ->
                databaseRows.add(MessageFormat.format(messageProperties.countRows,
                        dbName.value,
                        personRepository.countRows(dbName.value)))
            }
            return StatusTO(DatabaseStatus.UP.value, databaseRows)
        } catch (e: Exception) {
            throw GeneratorErrorException(messageProperties.errorServer)
        }
    }

    @Transactional
    open fun generateDataMatrixNameCPF(idiom: String): String {

        val firstnameList = loadFile(FIRST_NAME_FILE, idiom)
        val surnameList = loadFile(LAST_NAME_FILE, idiom)
        val personCPFList = generateUniquesCPF(firstnameList.size * surnameList.size)
        val personNameList = mutableListOf<Person>()

        try {
            async {
                cleanAllData()
            }
            logger.info(messageProperties.infoCreate)

            firstnameList.forEach { fn ->
                surnameList.forEach { sn ->
                    personNameList.add(Person("$fn $sn"))
                }
            }

            dataAuditRepository.insertOperationDataAudit(Operation.CREATE.value)
            async {
                insertAllData(personCPFList, personNameList)
            }
            return messageProperties.successGenerate
        } catch (e: Exception) {
            throw GeneratorErrorException(messageProperties.errorServer)
        }
    }

    private suspend fun cleanAllData(): List<Unit> {
        val executions: MutableList<Deferred<Unit>> = mutableListOf()
        DatabaseTable.values().forEach { dbName ->
            executions.add(async {
                logger.info(MessageFormat.format(messageProperties.infoCleaningDatabase, dbName.value))
                personRepository.cleanAllData(dbName.value)
                logger.info(MessageFormat.format(messageProperties.successCleaningDatabase, dbName.value))
            })
        }
        return executions.awaitAll()
    }

    private suspend fun insertAllData(personCPFList: MutableList<Person>, personNameList: MutableList<Person>): List<Unit> {
        val executions: MutableList<Deferred<Unit>> = mutableListOf()

        executions.add(async {
            logger.info(MessageFormat.format(messageProperties.infoGenerateDatabase, DatabaseTable.PERSON_CPF.value))
            personRepository.insertBatchPersonValues(DatabaseTable.PERSON_CPF.value, personCPFList)
            logger.info(MessageFormat.format(messageProperties.successGenerateDatabase, DatabaseTable.PERSON_CPF.value))
        })
        executions.add(async {
            logger.info(MessageFormat.format(messageProperties.infoGenerateDatabase, DatabaseTable.PERSON_NAME.value))
            personRepository.insertBatchPersonValues(DatabaseTable.PERSON_NAME.value, personNameList)
            logger.info(MessageFormat.format(messageProperties.successGenerateDatabase, DatabaseTable.PERSON_NAME.value))
        })

        return executions.awaitAll()
    }

    private fun generateUniquesCPF(numberCPF: Int): MutableList<Person> {

        val setListCPF = mutableSetOf<Person>()
        while (setListCPF.size < numberCPF) {
            setListCPF.add(Person(cpfGenerator.generateWithoutMask()))
        }

        return setListCPF.toMutableList()
    }

    private fun loadFile(fileName: String, idiom: String): List<String> {

        return if (directoryProperties.inputDirectoryPath.isNotEmpty()) {
            loadFileFromSystem(findInputFilePath(directoryProperties.inputDirectoryPath, fileName))
        } else {
            val fileResource = createFileResourcePath(fileName, idiom.toLowerCase())
            loadFileFromResource(fileResource)
        }
    }

    private fun loadFileFromSystem(filePath: String): MutableList<String> {
        logger.info { MessageFormat.format(messageProperties.infoLoading, filePath) }
        val lineList = mutableListOf<String>()
        File(filePath).useLines { lines -> lines.forEach { lineList.add(it) } }
        logger.info { MessageFormat.format(messageProperties.successLoading, filePath) }
        return lineList
    }

    private fun findInputFilePath(inputDirectoryPath: String, fileName: String): String {
        val externalFilePath = appendSeparator(inputDirectoryPath).plus(fileName)
        return if (File(inputDirectoryPath).canRead() &&
                File(inputDirectoryPath).isDirectory &&
                File(externalFilePath).isFile) externalFilePath else
            throw GeneratorErrorException(MessageFormat.format(messageProperties.errorExternal, externalFilePath))
    }

    fun loadFileFromResource(fileResource: String): MutableList<String> {
        try {

            logger.info { MessageFormat.format(messageProperties.infoLoading, fileResource) }
            val reader = BufferedReader(InputStreamReader(javaClass.getResourceAsStream(fileResource)))

            val lineList = mutableListOf<String>()

            reader.lines().forEach {
                lineList.add(it)
            }
            logger.info { MessageFormat.format(messageProperties.successLoading, fileResource) }
            return lineList
        } catch (e: Exception) {
            throw GeneratorErrorException(MessageFormat.format(messageProperties.errorFilepath, fileResource))
        }
    }

    private fun createFileResourcePath(fileName: String, idiom: String): String {
        return getLanguagePath(getEnvVariable(idiom)).append("/")
                .append(fileName).toString()
    }

    private fun getLanguagePath(language: String): StringBuilder {
        val languagePath = StringBuilder()

        try {
            javaClass.getResourceAsStream(RESOURCE_DIRECTORY + language).available()
            return languagePath.append(RESOURCE_DIRECTORY).append(language)
        } catch (npe: NullPointerException) {
            throw GeneratorErrorException(MessageFormat.format(messageProperties.errorIdiom, language))
        }
    }

    private fun getEnvVariable(envVariable: String): String {
        return if (envVariable.isNotEmpty()) envVariable else
            throw GeneratorErrorException(messageProperties.errorEnvironment)
    }

    private fun appendSeparator(path: String): String {
        return if (path.endsWith(File.separator, true)) path else path.plus(File.separator)
    }
}
