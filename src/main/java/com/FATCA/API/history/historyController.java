package com.FATCA.API.history;

import com.FATCA.API.user.Roles;
import com.FATCA.API.user.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
@Slf4j
public class historyController {
    private final HistoryService historyService;
    @GetMapping("/getuserhistory/{id}")
    public ResponseEntity<?> getUserHistory(@PathVariable("id") String id) throws Exception {
        return ResponseEntity.ok().body(historyService.getUserHistory(Long.parseLong(id)));

    }

}
