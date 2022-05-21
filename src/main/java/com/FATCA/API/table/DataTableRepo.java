package com.FATCA.API.table;

import com.FATCA.API.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DataTableRepo extends JpaRepository<DataTable, Long> {

    List<DataTable> findByOwner(AppUser owner);
}
