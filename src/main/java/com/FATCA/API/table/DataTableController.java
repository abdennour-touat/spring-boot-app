package com.FATCA.API.table;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
@Slf4j
public class DataTableController {
    private final DataTableService dataTableService;
    @GetMapping("/getcsvFiles")
    public ResponseEntity<?> getCsvFiles(){
        return ResponseEntity.ok().body(dataTableService.getAllTables());
    }
    @GetMapping("/getcsvFiles/{id}")
    public ResponseEntity<?> getCsvFile(@PathVariable("id") String id){
        Long identifier = Long.parseLong(id);
        System.out.println(dataTableService.getUserTables(identifier));

        return ResponseEntity.ok().body(dataTableService.getUserTables(identifier));

    }
}
