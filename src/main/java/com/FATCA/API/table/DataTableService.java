package com.FATCA.API.table;

import com.FATCA.API.controllers.XmlGen.CsvService;
import com.FATCA.API.user.AppUser;
import com.FATCA.API.user.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor

public class DataTableService  {
    //add a table
    //remove a table
    //get a table
    //get all tables
    //update a table
    private final DataTableRepo dataTableRepo;
    private final UserRepo userRepo;
    private final CsvService csvService;

    public List<DataTable> getAllTables(){
        return dataTableRepo.findAll();
    }
    public List<DataTable> getUserTables(Long id){
        AppUser owner = userRepo.getById(id);
       return  dataTableRepo.findByOwner(owner);
    }
    public void addTable(DataTable table) throws Exception {
        AppUser owner = userRepo.findByUsername(table.getOwner().getUsername());
        if (owner != null){
            dataTableRepo.save(table);
        }else {
          throw new Exception("user not found in the database");
        }
    }
    public void removeTable(Long id){
        Optional<DataTable> table = dataTableRepo.findById(id);
        table.ifPresent(dataTableRepo::delete);
    }
    public void setText(Long id , ArrayList<String[]> text){
        DataTable dataTable = dataTableRepo.getById(id);
        dataTable.setData(text);
        dataTableRepo.save(dataTable);
    }
    public ArrayList<String[]> csvToArray(MultipartFile file){
        return (ArrayList<String[]>) csvService.getCsvFile(file);
    }
    public DataTable updateTable(Long id, List<String[]> data){
        DataTable table = dataTableRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("table not found for this id :: " + id));
        table.setData((ArrayList<String[]>) data);

        return dataTableRepo.save(table);
    }
}
