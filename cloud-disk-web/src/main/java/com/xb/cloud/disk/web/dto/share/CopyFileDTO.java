package com.xb.cloud.disk.web.dto.share;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author xiabiao
 * @date 2022-04-13
 */
@Data
public class CopyFileDTO {

  @NotNull(message = "fileId can not be null.")
  private Long fileId;

  @NotNull(message = "targetFileId can not be null.")
  private Long targetFileId;

  @NotNull(message = "shareId can not be null.")
  private Long shareId;

  private String extractCode;
}
