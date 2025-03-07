package com.jmd0.events.data;

import java.util.List;

import com.jmd0.events.models.EventEntity;

public interface EventRepositoryInterface {

    List<EventEntity> findByOrganizerid(Long organizerid);
    List<EventEntity> findAll();
    void deleteById(Long id);
    EventEntity save(EventEntity event);
    EventEntity findById(Long id);
    boolean existsById(Long id);
    List<EventEntity> findByDescription(String description);
}
