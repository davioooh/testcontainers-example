package com.davioooh.data

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import java.sql.ResultSet

fun <R> Jdbi.queryForList(
    queryStr: String,
    queryParams: Map<String, Any> = mapOf(),
    resultMapper: (ResultSet) -> R
): List<R> =
    this.withHandleUnchecked { handle ->
        handle.createQuery(queryStr)
            .bindMap(queryParams)
            .map { rs, _ -> resultMapper(rs) }
            .list()
    }

fun <R> Jdbi.queryForOneOrNull(
    queryStr: String,
    queryParams: Map<String, Any> = mapOf(),
    resultMapper: (ResultSet) -> R
): R? =
    this.withHandleUnchecked { handle ->
        handle.createQuery(queryStr)
            .bindMap(queryParams)
            .map { rs, _ -> resultMapper(rs) }
            .findOne().orElse(null)
    }