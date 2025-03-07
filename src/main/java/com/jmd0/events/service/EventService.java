package com.jmd0.events.service;

import com.jmd0.events.data.EventRepositoryInterface;
import com.jmd0.events.models.EventEntity;
import com.jmd0.events.models.EventModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class EventService {

    @Autowired
    private EventRepositoryInterface eventRepository;

    public List<EventModel> findAll() {
        List<EventEntity> eventEntities = eventRepository.findAll();
        return convertToModels(eventEntities);
    }

    public List<EventModel> findByOrganizerid(String organizerid) {
        List<EventEntity> eventEntities = eventRepository.findByOrganizerid(Long.valueOf(organizerid));
        return convertToModels(eventEntities);
    }

    public EventModel save(EventModel event) {
        EventEntity eventEntity = convertToEntity(event);
        EventEntity savedEvent = eventRepository.save(eventEntity);
        return convertToModel(savedEvent);
    }

    public void delete(String id) {
        eventRepository.deleteById(Long.valueOf(id));
    }

    public EventModel updateEvent(String id, EventModel event) {
        event.setId(id);
        return save(event);
    }

    public EventModel findById(String id) {
        EventEntity eventEntity = eventRepository.findById(Long.valueOf(id));
        return convertToModel(eventEntity);
    }

    private List<EventModel> convertToModels(List<EventEntity> eventEntities) {
        List<EventModel> eventModels = new ArrayList<>();
        for (EventEntity eventEntity : eventEntities) {
            eventModels.add(convertToModel(eventEntity));
        }
        return eventModels;
    }

    private EventModel convertToModel(EventEntity eventEntity) {
        return new EventModel(
                eventEntity.getId().toString(),
                eventEntity.getName(),
                eventEntity.getDate(),
                eventEntity.getLocation(),
                eventEntity.getOrganizerid(),
                eventEntity.getDescription()
        );
    }

    private EventEntity convertToEntity(EventModel eventModel) {
        EventEntity eventEntity = new EventEntity();
        
        if (eventModel.getId() != null) {
            eventEntity.setId(Long.parseLong(eventModel.getId()));
        }
        
        eventEntity.setName(eventModel.getName());
    
        // Directly set the java.sql.Date without formatting
        eventEntity.setDate(new java.sql.Date(eventModel.getDate().getTime()));
    
        eventEntity.setLocation(eventModel.getLocation());
        eventEntity.setOrganizerid(eventModel.getOrganizerid());
        eventEntity.setDescription(eventModel.getDescription());
        
        return eventEntity;
    }
    

    public List<EventModel> findByDescription(String searchString) {
        List<EventEntity> eventEntities = eventRepository.findByDescription(searchString);
        return convertToModels(eventEntities);  
    }
}
