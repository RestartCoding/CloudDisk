package com.xb.cloud.disk.web.dto.share;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @author xiabiao
 * @date 2022-04-12
 */
@Data
public class CreateShareDTO {

  /** 文件 */
  @NotEmpty(message = "FileIds can not be empty.")
  private List<Long> fileIds;

  /** 过期时间 */
  private Long expiredTime;
}
