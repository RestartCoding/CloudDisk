package com.xb.cloud.disk.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xb.cloud.disk.core.ThreadToken;
import com.xb.cloud.disk.core.TokenManager;
import com.xb.cloud.disk.core.entity.FileInfo;
import com.xb.cloud.disk.core.entity.Share;
import com.xb.cloud.disk.core.entity.ShareFile;
import com.xb.cloud.disk.core.mapper.ShareMapper;
import com.xb.cloud.disk.core.service.FileService;
import com.xb.cloud.disk.core.service.ShareFileService;
import com.xb.cloud.disk.core.service.ShareService;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * @author xiabiao
 * @date 2022-04-12
 */
@Service
public class ShareServiceImpl extends ServiceImpl<ShareMapper, Share> implements ShareService {

  private static final Logger logger = LoggerFactory.getLogger(ShareServiceImpl.class);

  @Resource private ShareFileService shareFileService;

  @Resource private FileService fileService;

  @Resource private TokenManager tokenManager;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public long share(List<Long> fileIds, Date expiredTime, String extractCode) {
    List<FileInfo> fileInfos = fileService.listByIds(fileIds);
    if (fileInfos.size() != fileIds.size()) {
      logger.error("分享的文件中有部分文件找不到");
      throw new RuntimeException("分享的文件中有部分文件找不到");
    }
    // check permission
    String currUsername = tokenManager.load(ThreadToken.get()).getUsername();
    boolean permissionDenied = fileInfos.stream().anyMatch(f -> !f.getOwner().equals(currUsername));
    if (permissionDenied) {
      logger.error("Permission denied.");
      throw new RuntimeException("Permission denied.");
    }
    // save data
    Share share = new Share();
    share.setShareUser(currUsername);
    share.setExtractCode(extractCode);
    share.setExpiredTime(expiredTime);
    save(share);

    List<ShareFile> shareFileList =
        fileIds.stream()
            .map(
                e -> {
                  ShareFile shareFile = new ShareFile();
                  shareFile.setShareId(share.getShareId());
                  shareFile.setFileId(e);
                  return shareFile;
                })
            .collect(Collectors.toList());
    shareFileService.saveBatch(shareFileList);

    return share.getShareId();
  }

  @Override
  public List<FileInfo> listShareFiles(long shareId, String extractCode) {
    Share share = getById(shareId);

    checkShare(extractCode, share);

    List<Long> fileIds =
        shareFileService
            .list(new LambdaQueryWrapper<ShareFile>().eq(ShareFile::getShareId, shareId)).stream()
            .map(ShareFile::getFileId)
            .collect(Collectors.toList());
    return fileService.listByIds(fileIds);
  }

  @Override
  public long copy(long fileId, long targetFileId, long shareId, String extractCode) {
    Share share = getById(shareId);

    checkShare(extractCode, share);

    ShareFile shareFile =
        shareFileService.getOne(
            new LambdaQueryWrapper<ShareFile>()
                .eq(ShareFile::getShareId, shareId)
                .eq(ShareFile::getFileId, fileId));
    if (shareFile == null) {
      logger.error("Share file not found.");
      throw new RuntimeException("Share file not found.");
    }

    FileInfo targetFile = fileService.getById(targetFileId);
    if (targetFile == null) {
      logger.error("target file not found.");
      throw new RuntimeException("target file not found.");
    }
    if (targetFile.getIsFolder() != 1) {
      logger.error("target file is not a folder.");
      throw new RuntimeException("target file is not a folder.");
    }
    if (!targetFile.getOwner().equals(tokenManager.load(ThreadToken.get()).getUsername())) {
      log.error("permission denied.");
      throw new RuntimeException("permission denied");
    }

    FileInfo fileInfo = fileService.getById(fileId);
    fileInfo.setFileId(null);
    fileInfo.setParentId(targetFileId);
    fileService.save(fileInfo);
    return fileInfo.getFileId();
  }

  private void checkShare(String extractCode, Share share) {
    if (share == null) {
      logger.error("share not found");
      throw new RuntimeException("share not found.");
    }

    if (share.getExpiredTime().getTime() < System.currentTimeMillis()) {
      logger.error("share had been expired.");
      throw new RuntimeException("share had been expired.");
    }

    if (StringUtils.hasText(share.getExtractCode())
        && !share.getExtractCode().equals(extractCode)) {
      logger.error("incorrect extract code.");
      throw new RuntimeException("incorrect extract code.");
    }
  }

  @Override
  public InputStream download(long shareId, long fileId, String extractCode)
      throws FileNotFoundException {

    Share share = getById(shareId);
    checkShare(extractCode, share);

    ShareFile shareFile =
        shareFileService.getOne(
            new LambdaQueryWrapper<ShareFile>()
                .eq(ShareFile::getShareId, shareId)
                .eq(ShareFile::getFileId, fileId));
    if (shareFile == null) {
      logger.error("share file not found.");
      throw new RuntimeException("share file not found.");
    }

    FileInfo fileInfo = fileService.getById(fileId);
    if (fileInfo == null) {
      logger.error("file not found.");
      throw new RuntimeException("file not found.");
    }

    return new FileInputStream(fileInfo.getFilePath());
  }
}
