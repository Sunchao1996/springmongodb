package com.mongo.util;

import com.mongo.model.MongoSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * 就是根据传入的序列名称获取下一个序列值。
 * 通过findAndModify这个方法来保证序列唯一，因为这是一个原子操作。
 * 这段代码的原理就是，新建一个mongoSequence的Collection（如果没有才新增），然后根据传入的collectionName，根据_id即主键去找，如果有值，则+1，并返回修改后的值，如果没有则新增一条记录，并返回该值。
 * <p>
 * 如果改为关系数据库，则原理就是这样，新建一个表mongoSequence，里面两个字段_id,seq，当根据_id来取值时，如果没有记录，则新增一条记录，并返回1，如果有记录，则把seq+1，并返回修改后的记录。通过findAndModify来实现lock锁定这一行的目的。
 */
@Component
public class MongoAutoidUtil {
    @Autowired
    private MongoTemplate mongoTemplate;

    public int getNextSequence(String collectionName) {
        //如果没有mongoSequence的Collection则会根据实体类进行创建
        //查询mongoSequnce中是否有_id=collectionName的集合，upsert=true是如果没有的话根据前两个参数创建一个，之后更新seq+1，并返回更新之后的结果
        MongoSequence mongoSequence = mongoTemplate.findAndModify(
                query(where("_id").is(collectionName)), new Update().inc("seq", 1), FindAndModifyOptions.options().upsert(true).returnNew(true), MongoSequence.class
        );
        return mongoSequence.getSeq();
    }
}
