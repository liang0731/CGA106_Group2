package com.ezticket.web.users.controller;

import com.ezticket.web.users.pojo.Host;
import com.ezticket.web.users.repository.HostRepository;
import com.ezticket.web.users.service.HostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/host")
public class HostController {
    @Autowired
    private HostService hostService;
    @Autowired
    private HostRepository hostRepository;
    @GetMapping("/gh")
    public List<Host> getAllHost(){ return  hostService.getAllHost(); }

    @PostMapping("/{hostno}")
    public ResponseEntity<?> updateHost(@PathVariable("hostno")Integer hostno, @Valid @RequestBody Host newhost, BindingResult bindingResult){
        //測試前端編輯輸入的資料有沒有進來
        System.out.println("hostno: " + hostno);
        System.out.println("host: " + newhost.toString());

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        } else {
            Host updateHost = hostService.updateHost(hostno,newhost);
            return  ResponseEntity.ok(updateHost);
        }



    }

    @PostMapping("/insertHost")
    public ResponseEntity<?> createHost(@Valid @RequestBody Host newhost , BindingResult bindingResult) {
        System.out.println("前端要新增host資料的內容: " + newhost.toString());
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            System.out.println("新增host資料的格式有誤,將錯誤返回前端!");
            return ResponseEntity.badRequest().body(errors);
        } else {
            System.out.println("新增host資料成功!");
            newhost = hostRepository.save(newhost);
            return ResponseEntity.ok(newhost);
        }
    }
}
