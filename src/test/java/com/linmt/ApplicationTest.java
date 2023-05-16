package com.linmt;

import com.linmt.service.RemoteUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * User: Linmt
 * Date: 2023/5/4
 * Time: 13:02
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

    @Autowired
    private RemoteUserService remoteUserService;

    @Test
    public void test() {
        Object user = remoteUserService.getUser("5");
        System.out.println("getUser返回结果为" + user);
    }
}
