package com.example.hld.springbootdemo.HTTPTest;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class Test {

    private String info;

    @GetMapping("/get-info")
    @ResponseBody
    public String getInfo(@RequestParam(name="param",
    required = false,
    defaultValue = "param default value") String param) {
        return info + param;
    }

    @PostMapping("/post-info")
    @ResponseBody
    public void postInfo(@RequestParam(name="infoname",
    required = false,
    defaultValue = "default info") String info) {
        this.info = info;
    }
}
