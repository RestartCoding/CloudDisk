package com.xb.cloud.disk.web.controller;

import com.xb.cloud.disk.core.entity.FileInfo;
import com.xb.cloud.disk.core.service.ShareService;
import com.xb.cloud.disk.web.dto.share.CopyFileDTO;
import com.xb.cloud.disk.web.dto.share.CreateShareDTO;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiabiao
 * @date 2022-04-12
 */
@RestController
@RequestMapping("/share")
public class ShareController {

  @Resource private ShareService shareService;

  @PostMapping
  public String createShare(@RequestBody @Validated CreateShareDTO createShareDTO) {
    return String.valueOf(
        shareService.share(
            createShareDTO.getFileIds(),
            null == createShareDTO.getExpiredTime()
                ? null
                : new Date(createShareDTO.getExpiredTime()), createShareDTO.getExtractCode()));
  }

  @GetMapping("/listShareFiles")
  public List<FileInfo> listShareFiles(
      @RequestParam long shareId,
      @RequestParam(value = "extractCode", required = false) String extractCode) {
    return shareService.listShareFiles(shareId, extractCode);
  }

  @PostMapping("/copy")
  public String move(@RequestBody @Validated CopyFileDTO copyFileDTO) {
    return String.valueOf(
        shareService.copy(
            copyFileDTO.getFileId(),
            copyFileDTO.getTargetFileId(),
            copyFileDTO.getShareId(),
            copyFileDTO.getExtractCode()));
  }

  @GetMapping("/download")
  public void download(
      @RequestParam long shareId,
      @RequestParam long fileId,
      @RequestParam(value = "extractCode", required = false) String extractCode,
      HttpServletResponse response)
      throws IOException {
    response
        .getOutputStream()
        .write(shareService.download(shareId, fileId, extractCode).readAllBytes());
  }
}
