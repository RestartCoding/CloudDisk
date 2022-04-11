package dto.user;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @author xiabiao
 * @date 2022-04-11
 */
@Data
public class RegisterByPhoneDTO {

  @NotEmpty(message = "User name can not be empty.")
  private String username;

  @NotEmpty(message = "Password can not be empty.")
  private String password;

  @NotEmpty(message = "Phone can not be empty,")
  private String phone;

  @NotEmpty(message = "Code can not be empty")
  private String verifyCode;
}
