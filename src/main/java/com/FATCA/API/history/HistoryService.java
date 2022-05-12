package com.FATCA.API.history;


import com.FATCA.API.user.AppUser;
import com.FATCA.API.user.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class HistoryService {
    private final HistoryRepo historyRepo;
    private final UserRepo userRepo;

    public void saveHistory(History history ){
        historyRepo.save(history);
    }
    public List<History> getUserHistory(AppUser user){
        AppUser user1 = userRepo.getById(user.getId());
        return user1.getUserHistory();
    }
    public List<AppUser> getusers(History history){
        History history1 = historyRepo.getById(history.getId());
        return history1.getHistoryUsers();
    }
    public void deleteHistory(Long id){
        History history = historyRepo.getById(id);
        historyRepo.delete(history);
    }
}
