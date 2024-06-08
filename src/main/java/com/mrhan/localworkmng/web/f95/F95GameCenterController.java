package com.mrhan.localworkmng.web.f95;

import com.mrhan.localworkmng.core.f95.F95GameSearchService;
import com.mrhan.localworkmng.model.bo.F95GameFatInfo;
import com.mrhan.localworkmng.model.request.PageRequest;
import com.mrhan.localworkmng.model.request.f95.F95GameSearchParam;
import com.mrhan.localworkmng.model.response.PageResult;
import com.mrhan.localworkmng.util.ValidateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author yuhang
 * @Date 2024-06-08 13:30
 * @Description
 */
@RestController
@RequestMapping("/f95")
@Api(value = "F95游戏仓Controller", tags = {"F95", "游戏", "后台"})
public class F95GameCenterController {

    @Resource
    private F95GameSearchService f95GameSearchService;

    @PostMapping("/queryGame")
    @ApiOperation("游戏查询")
    public PageResult<F95GameFatInfo> query(@RequestBody PageRequest<F95GameSearchParam> request) {
        ValidateUtil.checkNotNull(request, "入参为空");
        if (request.getCondition() == null) {
            request.setCondition(new F95GameSearchParam());
        }
        return f95GameSearchService.query(request);
    }

}
