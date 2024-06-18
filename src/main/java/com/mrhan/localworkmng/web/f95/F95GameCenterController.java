package com.mrhan.localworkmng.web.f95;

import com.mrhan.localworkmng.core.f95.F95GameSearchService;
import com.mrhan.localworkmng.model.bo.F95GameFatInfo;
import com.mrhan.localworkmng.model.request.PageRequest;
import com.mrhan.localworkmng.model.request.f95.F95GameSearchParam;
import com.mrhan.localworkmng.model.response.ListResult;
import com.mrhan.localworkmng.model.response.PageResult;
import com.mrhan.localworkmng.model.response.f95.F95GamePrefixViewInfo;
import com.mrhan.localworkmng.model.response.f95.F95GameTagViewInfo;
import com.mrhan.localworkmng.model.response.f95.F95GameViewInfo;
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
@Api(value = "F95游戏仓Controller", tags = {"F95"})
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
    @PostMapping("/queryGameForView")
    @ApiOperation("游戏视图查询")
    public PageResult<F95GameViewInfo> queryForView(@RequestBody PageRequest<F95GameSearchParam> request) {
        ValidateUtil.checkNotNull(request, "入参为空");
        if (request.getCondition() == null) {
            request.setCondition(new F95GameSearchParam());
        }
//        return f95GameSearchService.query(request);
        return null;
    }

    @PostMapping("/queryAllTags")
    @ApiOperation("查询所有标签")
    public ListResult<F95GameTagViewInfo> queryAllTags() {
        return null;
    }

    @PostMapping("/queryAllPrefixes")
    @ApiOperation("查询所有普通前缀")
    public ListResult<F95GamePrefixViewInfo> queryAllPrefixes() {
        return null;
    }

    @PostMapping("/queryAllStatusPrefixes")
    @ApiOperation("查询所有游戏状态前缀")
    public ListResult<F95GamePrefixViewInfo> queryAllStatusPrefixes() {
        return null;
    }

    @PostMapping("/queryAllEnginePrefixes")
    @ApiOperation("查询所有引擎前缀")
    public ListResult<F95GamePrefixViewInfo> queryAllEnginePrefixes() {
        return null;
    }

}
