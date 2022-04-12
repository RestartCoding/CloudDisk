package com.xb.cloud.disk.web.dto;

import java.util.List;
import lombok.Data;

/**
 * @author xiabiao
 * @date 2022-04-08
 */
@Data
public class MoveFileDTO {

  private List<Long> srcFileIds;

  private Long targetFileId;
}
