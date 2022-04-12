package com.xb.cloud.disk.web.dto;

import lombok.Data;

/**
 * @author xiabiao
 * @date 2022-04-08
 */
@Data
public class CreateFolderDTO {

  private String fileName;

  private Long parentId;
}
