package com.xb.cloud.disk.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xb.cloud.disk.core.entity.FileInfo;
import com.xb.cloud.disk.core.mapper.FileMapper;
import com.xb.cloud.disk.core.service.FileService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author xiabiao
 * @date 2022-04-08
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FileInfo> implements FileService {

  private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

  @Transactional(rollbackFor = Exception.class)
  @Override
  public Object upload(FileInfo fileInfo, InputStream inputStream) throws IOException {
    FileInfo parentFileInfo = getById(fileInfo.getParentId());
    if (parentFileInfo == null) {
      logger.error("Parent file is not found.");
      throw new RuntimeException("Parent file is not found.");
    }
    if (parentFileInfo.getIsFolder() != 1) {
      logger.error("Upload file failed. Parent file is not a folder.");
      throw new RuntimeException("Upload file failed. Parent file is not a folder.");
    }
    String filePath = parentFileInfo.getFilePath() + File.separator + fileInfo.getFileName();
    File file = new File(filePath);
    if (file.exists()) {
      logger.error("File has been existed exists: {}", fileInfo);
      throw new RuntimeException("File has been existed.");
    }

    byte[] bytes = inputStream.readAllBytes();

    fileInfo.setFilePath(filePath);
    fileInfo.setIsFolder(0);
    // 保存到磁盘
    fileInfo.setFileSize((long) bytes.length);
    FileOutputStream fileOutputStream = new FileOutputStream(file);

    // 保存数据库
    save(fileInfo);
    fileOutputStream.write(bytes);
    fileOutputStream.flush();
    return fileInfo.getFileId();
  }

  @Override
  public InputStream download(String fileId) throws FileNotFoundException {
    FileInfo fileInfo = getById(fileId);
    if (fileInfo == null) {
      logger.error("Download file failed. File is not found.");
      throw new RuntimeException("File not found.");
    }
    return new FileInputStream(fileInfo.getFilePath());
  }

  @Override
  public List<FileInfo> listFile(String parentFileId) {
    return list(new LambdaQueryWrapper<FileInfo>().eq(FileInfo::getParentId, parentFileId));
  }

  @Override
  public Long createFolder(Long parentId, String folderName) throws IOException {
    FileInfo parentFileInfo = getById(parentId);
    if (parentFileInfo == null) {
      throw new RuntimeException("Parent file not found.");
    }
    if (parentFileInfo.getIsFolder() != 1) {
      throw new RuntimeException("Parent file is not a folder.");
    }

    String filePath = parentFileInfo.getFilePath() + File.separator + folderName;
    if (new File(filePath).exists()) {
      throw new RuntimeException("Folder has been existed.");
    }

    FileInfo fileInfo = new FileInfo();
    fileInfo.setParentId(parentId);
    fileInfo.setFilePath(filePath);
    fileInfo.setFileName(folderName);
    fileInfo.setIsFolder(1);
    save(fileInfo);

    // 创建文件夹
    Files.createDirectories(Paths.get(filePath));

    return fileInfo.getFileId();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void removeFile(Long fileId) throws IOException {

    // 不能删除根目录
    if (fileId == 1) {
      throw new RuntimeException("You can not delete root directory.");
    }
    FileInfo fileInfo = getById(fileId);
    // 删除j记录
    removeById(fileId);

    if (fileInfo.getIsFolder() == 1) {
      // 删除直接子目录的记录。直接子目录的记录删除之后，其他记录也找不到它们了。可以暂时先不删
      remove(new LambdaQueryWrapper<FileInfo>().eq(FileInfo::getParentId, fileId));
      File file = new File(fileInfo.getFilePath());
      File[] files = file.listFiles();
      if (files != null && files.length > 0){
        throw new RuntimeException("Directory is not empty.");
      }
    }
    // 删除文件
    Files.delete(Paths.get(fileInfo.getFilePath()));
  }
}
