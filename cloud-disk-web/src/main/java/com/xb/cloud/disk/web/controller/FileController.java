package com.xb.cloud.disk.web.controller;

import com.xb.cloud.disk.core.entity.FileInfo;
import com.xb.cloud.disk.core.service.FileService;
import dto.CreateFolderDTO;
import dto.UploadFileDTO;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiabiao
 * @date 2022-04-08
 */
@RestController
@RequestMapping("/file")
@AllArgsConstructor
@MapperScan(basePackages = {"com.xb.cloud.disk.core.mapper"})
public class FileController {

  private FileService fileService;

  /**
   * 上传文件
   *
   * @return fileId
   */
  @PostMapping("/upload")
  public Object upload(UploadFileDTO dto) throws IOException {
    FileInfo fileInfo = new FileInfo();
    fileInfo.setFileName(dto.getFile().getOriginalFilename());
    fileInfo.setParentId(dto.getParentId());
    return fileService.upload(fileInfo, dto.getFile().getInputStream());
  }

  /** 下载文件 */
  @GetMapping("/download")
  public void download(@RequestParam String fileId, HttpServletResponse response)
      throws IOException {
    InputStream inputStream = fileService.download(fileId);
    response.getOutputStream().write(inputStream.readAllBytes());
    inputStream.close();
  }

  /** 文件列表 */
  @GetMapping("/list")
  public List<FileInfo> fileList(@RequestParam String parentFileId) {
    return fileService.listFile(parentFileId);
  }

  @PostMapping("/folder")
  public String createFolder(@RequestBody CreateFolderDTO createFolderDTO) throws IOException {
    return String.valueOf(
        fileService.createFolder(createFolderDTO.getParentId(), createFolderDTO.getFileName()));
  }

  @DeleteMapping
  public void deleteFile(@RequestParam Long fileId) throws IOException {
    fileService.removeFile(fileId);
  }
}
