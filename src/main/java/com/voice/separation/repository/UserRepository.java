package com.voice.separation.repository;

import com.voice.separation.pojo.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
