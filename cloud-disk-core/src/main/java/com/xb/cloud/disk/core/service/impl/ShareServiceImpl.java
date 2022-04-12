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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  public long share(List<Long> fileIds, Date expiredTime) {
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
    share.setExtractCode(generateExtractCode());
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
  public List<FileInfo> listShareFiles(long shareId) {
    List<Long> fileIds =
        shareFileService
            .list(new LambdaQueryWrapper<ShareFile>().eq(ShareFile::getShareId, shareId)).stream()
            .map(ShareFile::getFileId)
            .collect(Collectors.toList());
    return fileService.listByIds(fileIds);
  }

  private String generateExtractCode() {
    return String.valueOf(System.currentTimeMillis());
  }
}
