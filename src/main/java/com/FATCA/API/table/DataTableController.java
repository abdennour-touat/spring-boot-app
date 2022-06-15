package com.FATCA.API.table;

import com.FATCA.API.history.HistoryService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
@Slf4j
public class DataTableController {
    private final DataTableService dataTableService;
    @GetMapping("/getcsvFiles")
    public ResponseEntity<List<DataTable>> getCsvFiles(){
        return ResponseEntity.ok().body(dataTableService.getAllTables());
    }
    @GetMapping("/getcsvFiles/{id}")
    public ResponseEntity<List<DataTable>> getCsvFile(@PathVariable("id") String id){
        Long identifier = Long.parseLong(id);
//        System.out.println(dataTableService.getUserTables(identifier));

        return ResponseEntity.ok().body(dataTableService.getUserTables(identifier));
    }
    @PutMapping("updateCsvFile/{id}")
    public ResponseEntity<?> updateCsvFile(@PathVariable("id") String id, @RequestBody UpdateData form){
        if(!form.getData().isEmpty()){
            return  ResponseEntity.ok().body(dataTableService.updateTable(Long.parseLong(id), form.getData(), form.getUpdateMessage(), form.getUserId()));
        }else {
            return ResponseEntity.badRequest().body("invalid information");
        }
    }
}

@Data
class UpdateData{
    private ArrayList<List<HashMap<String, String>>> data;
    private String updateMessage;
    private Long userId;
}