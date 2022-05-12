package com.FATCA.API.table;

import com.FATCA.API.history.History;
import com.FATCA.API.user.AppUser;
import com.FATCA.API.user.UserRepo;
import com.FATCA.API.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor

public class DataTableService {
    //add a table
    //remove a table
    //get a table
    //get all tables
    //update a table
    private final DataTableRepo dataTableRepo;
    private final UserRepo userRepo;

    public List<DataTable> getUserTables(AppUser user){
        AppUser owner = userRepo.getById(user.getId());
       return  owner.getTables();
    }
    public void addTable(DataTable table, AppUser user) throws Exception {
        AppUser owner = userRepo.findByUsername(user.getUsername());
        if (owner != null){
            if (table != null){
                dataTableRepo.save(table);
            }else{
                throw new Exception("table is empty");
            }
        }else {
          throw new Exception("user not found in the database");
        }
    }
    public void removeTable(Long id){
        Optional<DataTable> table = dataTableRepo.findById(id);
        table.ifPresent(dataTableRepo::delete);
    }
    public void setText(Long id , ArrayList<String> text){
        DataTable dataTable = dataTableRepo.getById(id);
        dataTable.setData(text);
        dataTableRepo.save(dataTable);
    }

}
