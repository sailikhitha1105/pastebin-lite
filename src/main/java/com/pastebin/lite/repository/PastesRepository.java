package com.pastebin.lite.repository;

import com.pastebin.lite.entity.Pastes;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class PastesRepository
{
    private final MongoTemplate mongoTemplate;

    public PastesRepository(MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
    }

    public Pastes save(Pastes paste)
    {
        return mongoTemplate.save(paste);
    }

    public Pastes findById(String id)
    {
        return mongoTemplate.findOne(new Query().addCriteria(Criteria.where("id").is(id).and("isActive").is(true)),Pastes.class);
    }

    public long count()
    {
        return mongoTemplate.count(new Query(),Pastes.class);
    }
}
