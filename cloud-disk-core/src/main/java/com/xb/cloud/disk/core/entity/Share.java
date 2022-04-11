package com.xb.cloud.disk.core.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author xiabiao
 * @date 2022-04-11
 */
@Data
public class Share {

  @TableId private String shareId;

  private String shareUser;

  private String shareFileInfoIds;
}
