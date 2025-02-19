package me.bsuir.easyattend.service;

import java.util.List;
import java.util.Optional;
import me.bsuir.easyattend.model.Event;



public interface EventService {
    Optional<Event> findById(Long id);

    List<Event> findAll();

    List<Event> findByNameContaining(String name);

    List<Event> findByLocationContaining(String location);

    Event save(Event event);

    void deleteById(Long id);
}