package com.davioooh.tctest.data

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
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

    @Test
    fun `finds a troll by ID`() {
        val troll = dao.findById(2)

        troll ?: fail { "Troll with id = 2 not found" }
        troll.name shouldBe "Branch"
    }

    @Test
    fun `finds all trolls`() {
        val allTrolls = dao.findAll()

        allTrolls.size shouldBe 4
        allTrolls.filter { it.isGlitterTroll }.size shouldBe 1
    }

    @Test
    fun `inserts a new troll`() {
        val smidgeId = dao.insert(
                TrollData(
                        name = "Smidge",
                        description = "Smidge is one of the smallest Trolls in Troll Kingdom. But is very very strong!",
                        isGlitterTroll = false
                )
        )

        assertOnRetrievedTroll(smidgeId) { smidge ->
            smidge ?: fail { "Smidge was not created correctly" }
            smidge["name"] shouldBe "Smidge"
            smidge["is_glitter_troll"] shouldBe false
        }
    }

    @Test
    fun `updates an existing troll`() {
        dao.update(4,
                TrollData(
                        name = "Biggie",
                        description = "Biggie is a big and fat troll. He carries around with him a pet worm named Mr. Dinkles.",
                        isGlitterTroll = false
                )
        )

        assertOnRetrievedTroll(4) { biggie ->
            biggie ?: fail { "Can't find Biggie" }
            (biggie["description"] as String) shouldContain "Mr. Dinkles"
        }
    }

    @Test
    fun `deletes a troll by ID`() {
        assertOnRetrievedTroll(4) { biggie ->
            biggie shouldNotBe null
        }

        dao.deleteById(4)

        assertOnRetrievedTroll(4) { biggie ->
            biggie shouldBe null
        }
    }

    private fun assertOnRetrievedTroll(trollId: Long, assertions: (Map<String, Any>?) -> Unit) {
        jdbi.useHandleUnchecked {
            val trollAsMap: Map<String, Any>? = it.createQuery("select * from $TEST_TABLE_NAME where id = $trollId")
                    .mapToMap()
                    .findOne().orElse(null)

            assertions(trollAsMap)
        }
    }

    companion object {
        const val DB_NAME = "test_db"
        const val DB_USER = "test"
        const val DB_PASSWORD = "test"
        const val TEST_TABLE_NAME = "trolls"

        fun buildJdbiFromTestContainer(postgresContainer: PostgreSQLContainer<Nothing>): Jdbi =
                postgresContainer.run {
                    Jdbi.create("jdbc:postgresql://$host:$firstMappedPort/$DB_NAME", username, password)
                }
    }

}
