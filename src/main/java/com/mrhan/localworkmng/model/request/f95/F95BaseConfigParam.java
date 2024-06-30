package com.mrhan.localworkmng.model.request.f95;

import com.mrhan.localworkmng.model.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author yuhang
 * @Date 2024-06-29 18:46
 * @Description
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class F95BaseConfigParam extends ToString {

    private String configStr;

}
