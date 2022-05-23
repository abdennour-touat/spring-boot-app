package com.FATCA.API.history;


import com.FATCA.API.user.AppUser;
import com.FATCA.API.user.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class HistoryService {
    private final HistoryRepo historyRepo;
    private final UserRepo userRepo;

    public void saveHistory(History history ){
        historyRepo.save(history);
    }
    public List<History> getUserHistory(Long id) throws Exception {
        Optional<AppUser> user1 = userRepo.findById(id);
        if(user1.isPresent()){
            return historyRepo.findByHistoryUser(user1.get());
        }else {
            throw new Exception("user not found");
        }
    }
    public AppUser getusers(History history){
        History history1 = historyRepo.getById(history.getId());
        return history1.getHistoryUser();
    }
    public void deleteHistory(Long id){
        History history = historyRepo.getById(id);
        historyRepo.delete(history);
    }
}
