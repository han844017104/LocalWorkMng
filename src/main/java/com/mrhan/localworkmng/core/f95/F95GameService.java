package com.mrhan.localworkmng.core.f95;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mrhan.localworkmng.dal.f95.mapper.F95GameMapper;
import com.mrhan.localworkmng.dal.f95.model.F95Game;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author yuhang
 * @Date 2024-06-29 13:50
 * @Description
 */
@Service
public class F95GameService {

    @Resource
    private F95GameMapper f95GameMapper;

    public F95Game queryByTid(String threadId) {
        return f95GameMapper.selectOne(
                Wrappers.<F95Game>lambdaQuery()
                        .eq(F95Game::getThreadId, threadId)
        );
    }

}
