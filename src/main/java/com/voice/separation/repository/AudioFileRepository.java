package com.voice.separation.repository;

import com.voice.separation.pojo.AudioFile;
import org.bson.types.Binary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface AudioFileRepository extends MongoRepository<AudioFile, String> {

    @Query(value = "{audioName: ?0}")
    public Optional<AudioFile> findAudio(String audioName);

    @Query(value = "{audioName: ?0, owner: ?1}")
    public Optional<AudioFile> findDeprecation(String audioName, String owner);

    @Query(value = "{audioId: ?0}")
    public Optional<AudioFile> findAudioById(String audioId);
}
