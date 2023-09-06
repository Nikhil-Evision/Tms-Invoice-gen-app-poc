package com.tms.invoiceapp.controller;

import com.tms.invoiceapp.model.WorkInfo;
import com.tms.invoiceapp.repository.WorkInfoRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class WorkinfoCheckController {
    @Autowired
    WorkInfoRepository workInfoRepository;
   @PostMapping("/test")
    public void getWorkInfo(@RequestParam List<Integer> workIds){
       Map<String, Integer> workidMap = new HashMap<>(); //Containing all workIds in this map
       for (int i = 0; i < workIds.size(); i++) {
           String variableName = "workId_" + (i + 1);
           int workid = workIds.get(i);
           workidMap.put(variableName, workid);
       }
        int workId= workidMap.get("workId_1");
        WorkInfo workInfo = workInfoRepository.findById(workId).get();
        System.out.println();
    }
}
