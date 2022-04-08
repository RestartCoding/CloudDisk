package com.xb.cloud.disk.core.initial;

import com.xb.cloud.disk.core.config.RootDirectoryConfigProperties;
import com.xb.cloud.disk.core.entity.FileInfo;
import com.xb.cloud.disk.core.service.FileService;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author xiabiao
 * @date 2022-04-08
 */
@Component
@AllArgsConstructor
public class InitRootDirectory implements ApplicationRunner {

  private RootDirectoryConfigProperties rootDirectoryConfigProperties;

  private FileService fileService;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    String rootDirectory = rootDirectoryConfigProperties.getRootDirectory();
    if (rootDirectory == null || rootDirectory.isEmpty()) {
      throw new RuntimeException("file.rootDirectory must be configured.");
    }
    File file = new File(rootDirectory);
    if (!file.exists()) {
      // 创建文件夹
      Files.createDirectory(Paths.get(rootDirectory));
    } else if (!file.isDirectory()) {
      throw new RuntimeException("Root directory must be a ");
    }

    String fileName = rootDirectory.substring(rootDirectory.lastIndexOf(File.separator) + 1);
    FileInfo fileInfo = new FileInfo();
    fileInfo.setFileId(1L);
    fileInfo.setFilePath(rootDirectory);
    fileInfo.setFileName(fileName);
    fileService.updateById(fileInfo);
  }
}
