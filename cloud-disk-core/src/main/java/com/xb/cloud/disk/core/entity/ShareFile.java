package com.xb.cloud.disk.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author xiabiao
 * @date 2022-04-12
 */
@Data
public class ShareFile {

  @TableId(type = IdType.AUTO)
  private Long id;

  private Long shareId;

  private Long fileId;
}
