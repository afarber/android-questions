/**
 * Room entity definition for dictionary words.
 *
 * This file defines the database table structure for storing words and their
 * explanations. Each language has its own database file, but they all use
 * this same entity structure.
 */
package com.wordsbyfarber.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single word entry in the dictionary database.
 *
 * This is a Room @Entity, which means Room will create a SQLite table
 * based on this class. Each instance of WordEntity becomes a row in the table.
 *
 * @property word The dictionary word (e.g., "AA", "HELLO"). This is the primary key,
 *                meaning each word must be unique in the table.
 * @property explanation The definition or explanation of the word. Defaults to empty
 *                       string if not provided.
 */
// @Entity tells Room this class represents a database table
// tableName specifies the actual table name in SQLite
@Entity(tableName = "words")
data class WordEntity(
    // @PrimaryKey marks this field as the unique identifier for each row
    // Using the word itself as the key ensures no duplicate words
    @PrimaryKey
    val word: String,

    // Default parameter value: if explanation is not provided, use empty string
    val explanation: String = ""
)
