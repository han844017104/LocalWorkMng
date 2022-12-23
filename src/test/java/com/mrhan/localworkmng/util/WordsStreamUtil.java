package com.mrhan.localworkmng.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mrhan.localworkmng.model.enums.TranslateEngineEnum;
import com.mrhan.localworkmng.model.request.PageRequest;
import com.mrhan.localworkmng.model.response.trans.TransLogGroup;
import com.mrhan.localworkmng.model.response.trans.TransTextDTO;
import com.mrhan.localworkmng.web.trans.TranslateBizController;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class WordsStreamUtil {

    @Resource
    private TranslateBizController translateBizController;

    private String from = "en";

    private String to = "zh";

    private String outputDir = "C:\\Users\\MrHan\\Desktop\\trans\\dataset\\20221223";

    private Set<String> usingEngines = Sets.newHashSet(
            TranslateEngineEnum.CUSTOM.name(),
            TranslateEngineEnum.GOOGLE.name(),
            TranslateEngineEnum.GOOGLE_GTX.name(),
            TranslateEngineEnum.TENCENT.name(),
            TranslateEngineEnum.AZURE.name(),
            TranslateEngineEnum.BAIDU.name()
    );

    @Test
    public void doStream() {
        long shardingSize = 100;
        int finishedSize = 0;
        for (long i = 0; ; i++) {
            List<TransLogGroup> list = loadSharding(shardingSize, i);
            System.out.println("load text: " + list.size());
            if (CollectionUtils.isEmpty(list)) {
                System.out.println("--------------------------------");
                System.out.println("process finished: " + finishedSize);
                return;
            }
            List<TransLogGroup> dataset = list.stream()
                    .map(this::split)
                    .flatMap(Collection::stream)
                    .filter(e -> CollectionUtils.isNotEmpty(e.getTrnasInfoList()))
                    .filter(this::doFilter)
                    .toList();
            System.out.println("filtered size: " + dataset.size());
            dataset.forEach(this::down);
            finishedSize += dataset.size();
            System.out.println("processed items: " + finishedSize);
        }
    }

    private List<TransLogGroup> split(TransLogGroup group) {
        if (CollectionUtils.isEmpty(group.getTrnasInfoList())) {
            return Lists.newArrayList();
        }
        return group.getTrnasInfoList().stream().filter(Objects::nonNull).map(e ->
                BeanUtil.copy(group, TransLogGroup.class,
                        copy -> copy.setTrnasInfoList(Lists.newArrayList(e)))
        ).collect(Collectors.toList());
    }

    private boolean doFilter(TransLogGroup group) {
        if (!StrUtil.equalsIgnoreCase(group.getFromLanguage(), from)) {
            return false;
        }
        TransTextDTO textDTO = group.getTrnasInfoList().get(0);
        if (!StrUtil.equalsIgnoreCase(textDTO.getToLanguage(), to)) {
            return false;
        }
        return usingEngines.contains(textDTO.getTransEngine());
    }

    private void down(TransLogGroup group) {
        File dir = FileUtil.file(outputDir);
        if (!dir.isDirectory()) {
            throw new RuntimeException("output dir is not a directory!");
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File sourceFile = FileUtil.newFile(outputDir + "\\" + from + ".txt");
        File targetFile = FileUtil.newFile(outputDir + "\\" + to + ".txt");
        FileUtil.appendUtf8Lines(Lists.newArrayList(StrUtil.replace(group.getTextOriginal(), "\u200B", "")),
                sourceFile);
        FileUtil.appendUtf8Lines(
                Lists.newArrayList(StrUtil.replace(group.getTrnasInfoList().get(0).getTextTrans(), "\u200B", "")),
                targetFile);
    }


    private List<TransLogGroup> loadSharding(long shardingSize, long shardingIdx) {
        PageRequest<TransLogGroup> request = new PageRequest<>();
        request.setPaged(true);
        request.setSize(shardingSize);
        request.setCurrentPage(shardingIdx + 1);
        return translateBizController.queryFrequentWords(request).getResults();
    }

}
