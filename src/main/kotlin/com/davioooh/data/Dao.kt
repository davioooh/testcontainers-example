package com.davioooh.data

interface Dao<in I, out O, ID> {
    fun findById(id: ID): O?
    fun findAll(): List<O>
    fun insert(input: I): ID
    fun update(id: ID, input: I)
    fun deleteById(id: ID)
}