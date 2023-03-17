package com.voice.separation.config;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GridFSConfig {

    @Value("${spring.data.mongodb.database}")
    private String db;

    @Bean
    public GridFSBucket getGridFSBucket(MongoClient mongoClient) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(db);
        GridFSBucket bucket = GridFSBuckets.create(mongoDatabase);
        return bucket;
    }
}
