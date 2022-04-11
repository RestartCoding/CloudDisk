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
public class User {

  @TableId(type = IdType.AUTO)
  private String userId;

  private String username;

  private String password;

  private String phone;

  private String email;

  private Date createTime;

  private Date updateTime;
}
