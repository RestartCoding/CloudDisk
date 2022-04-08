package com.xb.cloud.disk.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xb.cloud.disk.core.entity.FileInfo;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author xiabiao
 * @date 2022-04-08
 */
public interface FileService extends IService<FileInfo> {

  /**
   * 上传文件
   *
   * @return 上传结果
   */
  Object upload(FileInfo fileInfo, InputStream inputStream) throws IOException;

  /**
   * 下载文件
   *
   * @param fileId fileId
   * @return 输出流
   */
  InputStream download(String fileId) throws FileNotFoundException;

  /**
   * 文件列表
   *
   * @param parentFileId parentFileId
   * @return 文件列表
   */
  List<FileInfo> listFile(String parentFileId);

  /**
   * 创建文件夹
   *
   * @param parentId parentId
   * @param folderName folderName
   * @return fileId
   */
  Long createFolder(Long parentId, String folderName) throws IOException;

  /**
   * 删除文件
   *
   * @param fileId fileId
   */
  void removeFile(Long fileId) throws IOException;
}
