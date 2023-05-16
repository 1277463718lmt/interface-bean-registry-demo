package com.linmt.service;

import com.linmt.openfeign.annotations.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "user-server")
public interface RemoteUserService {

    @GetMapping("/getUser")
    Object getUser(@RequestParam String id);
}
