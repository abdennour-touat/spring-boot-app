package com.FATCA.API.user;

import com.FATCA.API.table.DataTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

//this interface add a new function to find a user by its username..
public interface UserRepo  extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
//    List<DataTable> getUserTables();
//    Set<DataTable> loadTables();
}
