package com.FATCA.API.history;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class HistoryService {
    private final HistoryRepo historyRepo;

    public void saveHistory(History history ){
        historyRepo.save(history);
    }
}
