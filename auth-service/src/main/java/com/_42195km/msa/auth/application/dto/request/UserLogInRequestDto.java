package com._42195km.msa.auth.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class UserLogInRequestDto {

	private String username;
	private String password;

}
