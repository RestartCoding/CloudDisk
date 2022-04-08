package dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author xiabiao
 * @date 2022-04-08
 */
@Data
public class UploadFileDTO {

  private Long parentId;

  private MultipartFile file;
}
