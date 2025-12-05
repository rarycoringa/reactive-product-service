package br.edu.ufrn.stock.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StockService {
    private final Logger logger = LoggerFactory.getLogger(StockService.class);

    public void createStock(String itemId) {}

    public void retrieveStock(String itemId) {}

    public void increaseStock(String itemId, Integer value) {}

    public void decreaseStock(String itemId, Integer value) {}

}
