package com.xb.cloud.disk.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xb.cloud.disk.core.entity.FileInfo;
import com.xb.cloud.disk.core.entity.Share;
import java.util.Date;
import java.util.List;

/**
 * @author xiabiao
 * @date 2022-04-12
 */
public interface ShareService extends IService<Share> {

  /**
   * 分享文件
   *
   * @param fileIds fileIds
   * @param expiredTime expiredTime
   * @return share id
   */
  long share(List<Long> fileIds, Date expiredTime);

  /**
   * 列出分享的文件
   *
   * @param shareId shareId
   * @return 分享的文件或文件夹
   */
  List<FileInfo> listShareFiles(long shareId);
}
