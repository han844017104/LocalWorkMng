package com.mrhan.localworkmng.model.request;

import com.mrhan.localworkmng.model.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author yuhang
 * @Date 2024-06-06 23:57
 * @Description
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class CommonSortOrder extends ToString {

    private String column;

    private boolean asc;

}
