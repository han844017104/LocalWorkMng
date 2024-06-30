package com.mrhan.localworkmng.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author yuhang
 * @Date 2024-06-04 21:54
 * @Description
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class F95GameFatInfo {

    private Long id;

    private String threadId;

    private String title;

    private String chTitle;

    private String gameVersion;

    private long views;

    private long likes;

    private double likeRatio;

    private double rating;

    private long gameUpdateDate;

    private long gmtCreate;

    private long gmtModified;

    private String status;

    private List<String> tagIds = new ArrayList<>();

    private List<String> prefixIds = new ArrayList<>();


}
