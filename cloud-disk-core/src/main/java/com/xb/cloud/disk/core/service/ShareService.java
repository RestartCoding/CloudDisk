package com.xb.cloud.disk.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xb.cloud.disk.core.entity.FileInfo;
import com.xb.cloud.disk.core.entity.Share;
import java.io.IOException;
import java.io.InputStream;
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
   * @param extractCode extractCode
   * @return share id
   */
  long share(List<Long> fileIds, Date expiredTime, String extractCode);

  /**
   * 列出分享的文件
   *
   * @param shareId shareId
   * @param extractCode extractCode
   * @return 分享的文件或文件夹
   */
  List<FileInfo> listShareFiles(long shareId, String extractCode);

  /**
   * 复制分享的文件到指定文件夹下
   *
   * @param fileId fileId
   * @param targetFileId targetFileId
   * @param shareId shareId
   * @param extractCode extractCode
   * @return 新文件的 fileId
   */
  long copy(long fileId, long targetFileId, long shareId, String extractCode);

  /**
   * 下载分享的文件
   *
   * @param shareId shareId
   * @param fileId fileId
   * @param extractCode extractCode
   * @return input stream
   * @throws IOException IOException
   */
  InputStream download(long shareId, long fileId, String extractCode) throws IOException;
}
