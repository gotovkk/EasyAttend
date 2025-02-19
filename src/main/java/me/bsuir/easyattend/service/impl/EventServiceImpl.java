package me.bsuir.easyattend.service.impl;

import java.util.List;
import java.util.Optional;
import me.bsuir.easyattend.dao.EventDao;
import me.bsuir.easyattend.model.Event;
import me.bsuir.easyattend.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EventServiceImpl implements EventService {

    private final EventDao eventDao;

    @Autowired
    public EventServiceImpl(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    @Override
    public Optional<Event> findById(Long id) {
        return eventDao.findById(id);
    }

    @Override
    public List<Event> findAll() {
        return eventDao.findAll();
    }

    @Override
    public List<Event> findByNameContaining(String name) {
        return eventDao.findByNameContaining(name);
    }

    @Override
    public List<Event> findByLocationContaining(String location) {
        return eventDao.findByLocationContaining(location);
    }

    @Override
    public Event save(Event event) {
        // Здесь можно добавить бизнес-логику, валидацию и т.д.
        return eventDao.save(event);
    }

    @Override
    public void deleteById(Long id) {
        eventDao.deleteById(id);
    }
}