package com._42195km.msa.auth.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class UserLogInRequestDto {

	@NotBlank(message = "아이디는 공백일 수 없습니다.")
	private String username;

	@NotBlank(message = "비밀번호는 공백일 수 없습니다.")
	private String password;

}
