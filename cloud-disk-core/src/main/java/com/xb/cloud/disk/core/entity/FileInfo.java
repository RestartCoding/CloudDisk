package com.xb.cloud.disk.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.util.Date;
import lombok.Data;

/**
 * @author xiabiao
 * @date 2022-04-08
 */
@Data
public class FileInfo {

  @TableId(type = IdType.AUTO)
  private Long fileId;

  private String filePath;

  private String fileName;

  private Long fileSize;

  private Integer isFolder;

  private Long parentId;

  private Date createTime;

  private Date updateTime;

  /** 文件所属者 */
  private String owner;
}
