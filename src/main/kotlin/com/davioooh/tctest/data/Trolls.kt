package com.davioooh.tctest.data

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.useHandleUnchecked
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.ZoneOffset

data class Troll(
    val id: Long,
    val name: String,
    val description: String,
    val isGlitterTroll: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class TrollData(
    val name: String,
    val description: String,
    val isGlitterTroll: Boolean
)

class Trolls(private val jdbi: Jdbi) : Dao<TrollData, Troll, Long> {
    private val tableName = "trolls"

    private val resultMapper =
        { rs: ResultSet ->
            Troll(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBoolean("is_glitter_troll"),
                LocalDateTime.ofInstant(rs.getTimestamp("created_at").toInstant(), ZoneOffset.UTC),
                LocalDateTime.ofInstant(rs.getTimestamp("updated_at").toInstant(), ZoneOffset.UTC)
            )
        }

    override fun findById(id: Long): Troll? =
        jdbi.queryForOneOrNull("select * from $tableName where id = :id", mapOf("id" to id), resultMapper)

    override fun findAll(): List<Troll> =
        jdbi.queryForList("select * from $tableName", resultMapper = resultMapper)

    override fun insert(input: TrollData): Long =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createUpdate(
                    """
                        insert into $tableName (name, description, is_glitter_troll)
                        values (:name, :description, :isGlitter)
                    """.trimIndent()
                )
                .bind("name", input.name)
                .bind("description", input.description)
                .bind("isGlitter", input.isGlitterTroll)
                .executeAndReturnGeneratedKeys()
                .map { rs, _ -> resultMapper(rs) }
                .one().id
        }

    override fun update(id: Long, input: TrollData) {
        jdbi.useHandleUnchecked { handle ->
            val updatedCount = handle
                .createUpdate(
                    """
                       update $tableName set 
                            name = :name,
                            description = :description,
                            is_glitter_troll = :isGlitter
                       where id = :id
                    """.trimIndent()
                )
                .bind("name", input.name)
                .bind("description", input.description)
                .bind("isGlitter", input.isGlitterTroll)
                .bind("id", id)
                .execute()
            if (updatedCount == 0) {
                throw IllegalArgumentException("Cannot update troll with id = $id in table $tableName: id not found")
            }
        }
    }

    override fun deleteById(id: Long) =
        jdbi.useHandleUnchecked {
            it.createUpdate("delete from $tableName where id = :id").bind("id", id).execute()
        }

}