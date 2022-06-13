package com.example.hld.springbootdemo.controller;


import com.example.hld.springbootdemo.dbFiles.msg_metadata;
import com.example.hld.springbootdemo.dbFiles.msgDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DbController {

    @Autowired
    private msgDataRepository repository;

    @RequestMapping(value="info/{id}",method = RequestMethod.GET)
    public msg_metadata getInfoById(@PathVariable Long id) {
        /*msg_metadata data = repository.findById(id).get();
        return data.getMsg_id();*/
        return repository.findById(id).get();
    }
}
