package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cache")
public class CacheController {

    @Autowired
    private CacheManager cacheManager;

    @DeleteMapping("/clear")
    public String clearAllCaches() {
        cacheManager.getCacheNames()
                .forEach(name -> {
                    if (cacheManager.getCache(name) != null) {
                        cacheManager.getCache(name).clear();
                    }
                });
        return "All caches cleared!";
    }
}
