package com.davioooh.data

import io.kotest.matchers.shouldBe
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.useHandleUnchecked
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
internal class TrollsTest {

    @Container
    private val postgreSQLContainer =
            PostgreSQLContainer<Nothing>("postgres:11.7")
                    .apply {
                        withDatabaseName(DB_NAME)
                        withUsername(DB_USER)
                        withPassword(DB_PASSWORD)
                    }

    private lateinit var jdbi: Jdbi
    private lateinit var dao: Trolls

    @BeforeEach
    fun setup() {
        jdbi = buildJdbiFromTestContainer(postgreSQLContainer)
        dao = Trolls(jdbi)
        jdbi.useHandleUnchecked { it.execute(ResourceFetcher.readResource("${TEST_TABLE_NAME}_test.sql")) }
    }

    private fun buildJdbiFromTestContainer(postgresContainer: PostgreSQLContainer<Nothing>): Jdbi =
            postgresContainer.run {
                Jdbi.create("jdbc:postgresql://$host:$firstMappedPort/$DB_NAME", username, password)
            }

    @Test
    fun `finds a troll by ID`() {
        val result = dao.findById(2)

        result shouldBe null
    }

    @Test
    fun `finds all trolls`() {
        fail("")
    }

    @Test
    fun `inserts a new troll`() {
        fail("")
    }

    @Test
    fun `updates an existing troll`() {
        fail("")
    }

    @Test
    fun `deletes a troll by ID`() {
        fail("")
    }

    companion object {
        const val DB_NAME = "test_db"
        const val DB_USER = "test"
        const val DB_PASSWORD = "test"
        const val TEST_TABLE_NAME = "trolls"
    }

}
