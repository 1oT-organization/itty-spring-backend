package org.iot.itty.login.controller;

import org.iot.itty.dto.UserDTO;
import org.iot.itty.login.service.LoginService;
import org.iot.itty.login.vo.RequestRegist;
import org.iot.itty.login.vo.RequestWithdrawal;
import org.iot.itty.login.vo.ResponseRegist;
import org.iot.itty.login.vo.ResponseWithdrawal;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/")
@Slf4j
public class LoginController {
	private final LoginService loginService;
	private final ModelMapper modelMapper;

	@Autowired
	public LoginController(LoginService loginService, ModelMapper modelMapper) {
		this.loginService = loginService;
		this.modelMapper = modelMapper;
	}

	@GetMapping("/health_check")
	public String healthCheck() {
		return "check check";
	}

	@PostMapping("/regist")
	public ResponseEntity<ResponseRegist> registUser(@RequestBody RequestRegist requestRegist) {

		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		UserDTO userDTO = modelMapper.map(requestRegist, UserDTO.class);

		int userCode = loginService.registUser(userDTO);

		ResponseRegist responseRegist = new ResponseRegist();
		responseRegist.setUserCodePk(userCode);
		responseRegist.setUserEmail(requestRegist.getUserEmail());
		responseRegist.setMessage("회원 가입 성공");

		return ResponseEntity.status(HttpStatus.CREATED).body(responseRegist);
	}

	@PutMapping("/user/withdrawal")
	public ResponseEntity<ResponseWithdrawal> userWithdrawal(@RequestBody RequestWithdrawal requestWithdrawal) {

		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		UserDTO userDTO = modelMapper.map(requestWithdrawal, UserDTO.class);

		ResponseWithdrawal responseWithdrawal = new ResponseWithdrawal();
		responseWithdrawal.setUserEmail(requestWithdrawal.getUserEmail());

		boolean isWithdrawalSuccessful = loginService.userWithdrawal(userDTO);

		if (isWithdrawalSuccessful) {
			responseWithdrawal.setMessage("회원 탈퇴 완료");

			return ResponseEntity.status(HttpStatus.OK).body(responseWithdrawal);
		} else {
			responseWithdrawal.setMessage("이미 탈퇴한 회원입니다.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWithdrawal);
		}
	}
}
