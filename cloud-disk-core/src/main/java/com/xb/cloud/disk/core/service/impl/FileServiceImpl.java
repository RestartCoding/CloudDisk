package com.xb.cloud.disk.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xb.cloud.disk.core.ThreadToken;
import com.xb.cloud.disk.core.TokenManager;
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
import java.util.UUID;
import javax.annotation.Resource;
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

  @Resource private TokenManager tokenManager;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public Object upload(FileInfo fileInfo, InputStream inputStream) throws IOException {
    FileInfo parentFileInfo = getById(fileInfo.getParentId());
    if (parentFileInfo == null) {
      logger.error("Parent file is not found.");
      throw new RuntimeException("Parent file is not found.");
    }

    if (parentFileInfo.getIsFolder() != 1) {
      throw new RuntimeException("Parent file must be a folder.");
    }

    if (fileInfo.getParentId() != 1
        && !parentFileInfo.getOwner().equals(tokenManager.load(ThreadToken.get()).getUsername())) {
      throw new RuntimeException("Can not upload file to other people's folder.");
    }

    if (parentFileInfo.getIsFolder() != 1) {
      logger.error("Upload file failed. Parent file is not a folder.");
      throw new RuntimeException("Upload file failed. Parent file is not a folder.");
    }
    byte[] bytes = inputStream.readAllBytes();
    String filePath = storage(bytes);

    fileInfo.setFilePath(filePath);
    fileInfo.setFileSize((long) bytes.length);
    fileInfo.setIsFolder(0);
    fileInfo.setOwner(tokenManager.load(ThreadToken.get()).getUsername());

    // 保存数据库
    save(fileInfo);
    return fileInfo.getFileId();
  }

  @Override
  public InputStream download(String fileId) throws FileNotFoundException {
    FileInfo fileInfo = getById(fileId);

    if (fileInfo == null) {
      logger.error("Download file failed. File is not found.");
      throw new RuntimeException("File not found.");
    }

    if (!fileInfo.getOwner().equals(tokenManager.load(ThreadToken.get()).getUsername())) {
      throw new RuntimeException("unauthorized");
    }

    return new FileInputStream(fileInfo.getFilePath());
  }

  @Override
  public List<FileInfo> listFile(String parentFileId) {
    return list(
        new LambdaQueryWrapper<FileInfo>()
            .eq(FileInfo::getParentId, parentFileId)
            .eq(FileInfo::getOwner, tokenManager.load(ThreadToken.get()).getUsername()));
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

    if (parentFileInfo.getFileId() != 1
        && !parentFileInfo.getOwner().equals(tokenManager.load(ThreadToken.get()).getUsername())) {
      throw new RuntimeException("Can not create a folder in other people's folder.");
    }

    String filePath = getFilePath();
    if (new File(filePath).exists()) {
      throw new RuntimeException("Folder has been existed.");
    }

    FileInfo fileInfo = new FileInfo();
    fileInfo.setParentId(parentId);
    fileInfo.setFilePath(filePath);
    fileInfo.setFileName(folderName);
    fileInfo.setIsFolder(1);
    fileInfo.setOwner(tokenManager.load(ThreadToken.get()).getUsername());
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

    if (fileInfo == null) {
      throw new RuntimeException("File not found.");
    }

    if (!fileInfo.getOwner().equals(tokenManager.load(ThreadToken.get()).getUsername())) {
      throw new RuntimeException("Can not delete other people's file.");
    }

    // 删除j记录
    removeById(fileId);

    if (fileInfo.getIsFolder() == 1) {
      // 删除直接子目录的记录。直接子目录的记录删除之后，其他记录也找不到它们了。可以暂时先不删
      remove(new LambdaQueryWrapper<FileInfo>().eq(FileInfo::getParentId, fileId));
      File file = new File(fileInfo.getFilePath());
      File[] files = file.listFiles();
      if (files != null && files.length > 0) {
        throw new RuntimeException("Directory is not empty.");
      }
    }
    // 删除文件
    Files.delete(Paths.get(fileInfo.getFilePath()));
  }

  @Override
  public void moveFile(List<Long> srcFileIds, Long dstFileId) {

    if (srcFileIds.contains(1L)) {
      throw new RuntimeException("You can not move root directory.");
    }
    List<FileInfo> srcFiles = listByIds(srcFileIds);
    FileInfo dstFile = getById(dstFileId);

    if (dstFile.getIsFolder() != 1) {
      throw new RuntimeException("Dst file is not a folder.");
    }

    // check permission
    String username = tokenManager.load(ThreadToken.get()).getUsername();
    boolean hasFileOfOtherPeople = srcFiles.stream().anyMatch(f -> !f.getOwner().equals(username));
    if (hasFileOfOtherPeople) {
      throw new RuntimeException("Can not move other people's file.");
    }

    if (dstFile.getFileId() != 1 && !dstFile.getOwner().equals(username)) {
      throw new RuntimeException("Can not move files to other people's folder");
    }

    srcFiles.forEach(e -> e.setParentId(dstFileId));
    updateBatchById(srcFiles);
  }

  private String getFilePath() {
    return getById(1).getFilePath() + File.separator + UUID.randomUUID().toString();
  }

  /**
   * 存储文件
   *
   * @param bytes bytes
   * @return 文件路径
   */
  private String storage(byte[] bytes) throws IOException {
    String filePath = getFilePath();
    try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
      fileOutputStream.write(bytes);
    }
    return filePath;
  }
}
