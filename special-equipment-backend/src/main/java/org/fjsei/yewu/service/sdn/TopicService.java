package org.fjsei.yewu.service.sdn;

import org.fjsei.yewu.entity.sdn.son.Topic;
import org.fjsei.yewu.entity.sdn.son.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TopicService {

    @Autowired
    private TopicRepository topicRepository;

    public List<Topic> getAllTopics() {
        List<Topic> topics = new ArrayList<>();
        topicRepository.findAll().forEach(topics::add);
        return topics;
    }

    public Optional<Topic>  getTopic(Long id) {
        //return topics.stream().filter(sei -> sei.getId().equals(id)).findFirst().get();
        return topicRepository.findById(id);
    }


    @PersistenceContext(unitName = "entityManagerFactorySdn")
    private EntityManager emFoo;

    @Transactional
    public void addTopic(Topic topic) {
        if(!emFoo.isJoinedToTransaction())      System.out.println("没达到 em.isJoinedToTransaction()");
        else System.out.println("到 em.isJoinedToTransaction()");
        if(!emFoo.isJoinedToTransaction())      emFoo.joinTransaction();
        Assert.isTrue(emFoo.isJoinedToTransaction(),"没isJoinedToTransaction");

        topicRepository.save(topic);
    }

    @Transactional
    public void updateTopic(String id, Topic topic) {
        topicRepository.save(topic);
    }

    public void deleteTopic(Long id) {
        topicRepository.deleteById(id);
    }

}
