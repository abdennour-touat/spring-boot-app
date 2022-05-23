package com.FATCA.API.history;

import com.FATCA.API.table.DataTable;
import com.FATCA.API.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryRepo extends JpaRepository<History, Long> {
    List<History> findByHistoryUser(AppUser owner);
}
