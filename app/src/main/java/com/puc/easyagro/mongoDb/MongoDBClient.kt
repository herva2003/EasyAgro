package com.puc.easyagro.mongoDb

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document

class MongoDBClient {
    private val mongoClient: MongoClient
    private val database: MongoDatabase

    init {
        // Configure a URI para o servidor MongoDB
        val uri = MongoClientURI("mongodb+srv://herva:lelinho1@app.eldeomh.mongodb.net/?retryWrites=true&w=majority")

        // Crie o cliente MongoDB
        mongoClient = MongoClient(uri)

        // Obtenha o banco de dados
        database = mongoClient.getDatabase("catalog")
    }

    fun getCollection(collectionName: String): MongoCollection<Document> {
        return database.getCollection(collectionName)
    }

    fun close() {
        mongoClient.close()
    }
}
