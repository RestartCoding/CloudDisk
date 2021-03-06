package com.xb.cloud.disk.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xb.cloud.disk.common.constant.SysConstant;
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

    if (!parentFileInfo.getIsFolder()) {
      throw new RuntimeException("Parent file must be a folder.");
    }

    if (fileInfo.getParentId() != SysConstant.ROOT_DIR_ID
        && !parentFileInfo.getOwner().equals(tokenManager.load(ThreadToken.get()).getUsername())) {
      throw new RuntimeException("Can not upload file to other people's folder.");
    }

    if (!parentFileInfo.getIsFolder()) {
      logger.error("Upload file failed. Parent file is not a folder.");
      throw new RuntimeException("Upload file failed. Parent file is not a folder.");
    }
    byte[] bytes = inputStream.readAllBytes();
    String filePath = storage(bytes);

    fileInfo.setFilePath(filePath);
    fileInfo.setFileSize((long) bytes.length);
    fileInfo.setIsFolder(false);
    fileInfo.setOwner(tokenManager.load(ThreadToken.get()).getUsername());

    // ???????????????
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
    if (!parentFileInfo.getIsFolder()) {
      throw new RuntimeException("Parent file is not a folder.");
    }

    if (parentFileInfo.getFileId() != SysConstant.ROOT_DIR_ID
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
    fileInfo.setIsFolder(true);
    fileInfo.setOwner(tokenManager.load(ThreadToken.get()).getUsername());
    save(fileInfo);

    // ???????????????
    Files.createDirectories(Paths.get(filePath));

    return fileInfo.getFileId();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void removeFile(Long fileId) throws IOException {

    // ?????????????????????
    if (fileId == SysConstant.ROOT_DIR_ID) {
      throw new RuntimeException("You can not delete root directory.");
    }
    FileInfo fileInfo = getById(fileId);

    if (fileInfo == null) {
      throw new RuntimeException("File not found.");
    }

    if (!fileInfo.getOwner().equals(tokenManager.load(ThreadToken.get()).getUsername())) {
      throw new RuntimeException("Can not delete other people's file.");
    }

    // ??????j??????
    removeById(fileId);

    if (fileInfo.getIsFolder()) {
      // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
      remove(new LambdaQueryWrapper<FileInfo>().eq(FileInfo::getParentId, fileId));
      File file = new File(fileInfo.getFilePath());
      File[] files = file.listFiles();
      if (files != null && files.length > 0) {
        throw new RuntimeException("Directory is not empty.");
      }
    }
    // ????????????
    Files.delete(Paths.get(fileInfo.getFilePath()));
  }

  @Override
  public void moveFile(List<Long> srcFileIds, Long dstFileId) {

    if (srcFileIds.contains(SysConstant.ROOT_DIR_ID)) {
      throw new RuntimeException("You can not move root directory.");
    }
    List<FileInfo> srcFiles = listByIds(srcFileIds);
    FileInfo dstFile = getById(dstFileId);

    if (dstFile.getIsFolder()) {
      throw new RuntimeException("Dst file is not a folder.");
    }

    // check permission
    String username = tokenManager.load(ThreadToken.get()).getUsername();
    boolean hasFileOfOtherPeople = srcFiles.stream().anyMatch(f -> !f.getOwner().equals(username));
    if (hasFileOfOtherPeople) {
      throw new RuntimeException("Can not move other people's file.");
    }

    if (dstFile.getFileId() != SysConstant.ROOT_DIR_ID && !dstFile.getOwner().equals(username)) {
      throw new RuntimeException("Can not move files to other people's folder");
    }

    srcFiles.forEach(e -> e.setParentId(dstFileId));
    updateBatchById(srcFiles);
  }

  private String getFilePath() {
    return getById(SysConstant.ROOT_DIR_ID).getFilePath()
        + File.separator
        + UUID.randomUUID().toString();
  }

  /**
   * ????????????
   *
   * @param bytes bytes
   * @return ????????????
   */
  private String storage(byte[] bytes) throws IOException {
    String filePath = getFilePath();
    try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
      fileOutputStream.write(bytes);
    }
    return filePath;
  }
}
