package com.example.demo.history;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class HistoryService {
    private final HistoryRepo historyRepo;

    public void saveHistory(History history ){
        historyRepo.save(history);
    }
}
