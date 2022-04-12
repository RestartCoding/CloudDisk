package com.xb.cloud.disk.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.util.Date;
import lombok.Data;

/**
 * @author xiabiao
 * @date 2022-04-11
 */
@Data
public class Share {

  @TableId(type = IdType.AUTO)
  private Long shareId;

  /** 分享人 */
  private String shareUser;

  /** 提取码 */
  private String extractCode;

  /** 过期时间 */
  private Date expiredTime;

  private Date createTime;

  private Date updateTime;
}
