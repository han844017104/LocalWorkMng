package com.mrhan.localworkmng.model.request.f95;

import com.mrhan.localworkmng.model.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.List;

/**
 * @Author yuhang
 * @Date 2024-06-15 13:25
 * @Description
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class ItemCondition extends ToString {

    @Serial
    private static final long serialVersionUID = -2955666059743753096L;

    private String type;

    private List<String> values;

    private String op;


}
