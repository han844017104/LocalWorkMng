/*
 * F5Loser
 * Copyright (c) 2021-2022 All Rights Reserved
 */
package com.mrhan.localworkmng.util;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * @Author yuhang
 * @Date 2022-08-09 18:14
 * @Description
 */
public class CodeGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create(
                        "jdbc:mysql://localhost:3306/f95_data?useSSL=false&useUnicode=true&characterEncoding=UTF8&autoReconnect=true",
                        "root", "Han237912")
                .globalConfig(builder -> {
                    builder.author("MrHan") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir(
                                    "T:\\WorkSpace\\LocalWorkMng\\src\\main\\java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.mrhan.localworkmng.dal") // 设置父包名
                            .moduleName("f95") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml,
                                    "T:\\WorkSpace\\LocalWorkMng\\src\\main\\java\\com\\mrhan\\localworkmng\\dal\\mapper")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("f95_game") // 设置需要生成的表名
                            .addInclude("f95_game_prefix") // 设置需要生成的表名
                            .addInclude("f95_game_relation") // 设置需要生成的表名
                            .addInclude("f95_game_tag") // 设置需要生成的表名
                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
