package me.bsuir.easyattend.logs;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class VisitCounter {

    private final Map<String, AtomicLong> visitCounts = new ConcurrentHashMap<>();

    public void incrementVisit(String url) {
        visitCounts.computeIfAbsent(url, k -> new AtomicLong(0)).incrementAndGet();
    }

    public long getVisitCount(String url) {
        AtomicLong counter = visitCounts.get(url);
        return counter != null ? counter.get() : 0;
    }
}