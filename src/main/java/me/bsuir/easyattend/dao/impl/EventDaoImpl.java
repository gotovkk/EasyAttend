package me.bsuir.easyattend.dao.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import me.bsuir.easyattend.dao.EventDao;
import me.bsuir.easyattend.model.Event;
import org.springframework.stereotype.Repository;


@Repository
public class EventDaoImpl implements EventDao {

    private final ConcurrentMap<Long, Event> events = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public EventDaoImpl() {
        save(
            new Event(
            null,
            "Java Meetup",
            "Minsk",
            "Discussion about Java",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(1).plusHours(2)));
        save(
            new Event(
            null,
            "C++ Meetup",
            "Gomel",
            "Discussion about C++",
            LocalDateTime.now().plusDays(2),
            LocalDateTime.now().plusDays(2).plusHours(2)));
        save(
            new Event(
            null,
            "Frontend Meetup",
            "Minsk",
            "Discussion about Frontend",
            LocalDateTime.now().plusDays(3),
            LocalDateTime.now().plusDays(3).plusHours(2)));
        save(
            new Event(
            null,
            "Python Meetup",
            "Brest",
            "Discussion about Python",
            LocalDateTime.now().plusDays(4),
            LocalDateTime.now().plusDays(4).plusHours(2)));
    }

    @Override
    public Optional<Event> findById(Long id) {
        return Optional.ofNullable(events.get(id));
    }

    @Override
    public List<Event> findAll() {
        return new ArrayList<>(events.values());
    }

    @Override
    public List<Event> findByNameContaining(String name) {
        return events.values().stream()
                .filter(event -> event.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }

    @Override
    public List<Event> findByLocationContaining(String location) {
        return events.values().stream()
                .filter(event -> event.getLocation().toLowerCase().contains(location.toLowerCase()))
                .toList();
    }

    @Override
    public Event save(Event event) {
        if (event.getId() == null) {
            event.setId(nextId.getAndIncrement());
        }
        events.put(event.getId(), event);
        return event;
    }

    @Override
    public void deleteById(Long id) {
        events.remove(id);
    }
}