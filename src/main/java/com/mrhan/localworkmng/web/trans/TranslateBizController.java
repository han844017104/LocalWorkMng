/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.web.trans;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author yuhang
 * @Date 2022-08-22 11:05
 * @Description
 */
@RestController
@RequestMapping("/trans")
public class TranslateBizController {

    @PostMapping("/test")
    public String test() {
        throw new RuntimeException("");
//        return "tttttttttttt";
    }

}
