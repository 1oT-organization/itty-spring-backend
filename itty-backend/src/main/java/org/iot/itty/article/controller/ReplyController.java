package org.iot.itty.article.controller;

import java.util.ArrayList;
import java.util.List;

import org.iot.itty.article.service.ReplyService;
import org.iot.itty.article.vo.ResponseSelectAllReplyByUserCodeFk;
import org.iot.itty.article.vo.ResponseSelectReplyByArticleCodeFk;
import org.iot.itty.dto.ReplyDTO;
import org.iot.itty.user.service.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ReplyController {

	private ReplyService replyService;
	private UserService userService;
	private ModelMapper mapper;

	@Autowired
	public ReplyController(ReplyService replyService, UserService userService, ModelMapper mapper) {
		this.replyService = replyService;
		this.userService = userService;
		this.mapper = mapper;
	}

	@GetMapping("/reply/article/{articleCodeFk}")
	public ResponseEntity<List<ResponseSelectReplyByArticleCodeFk>> selectReplyByArticleCodeFk(@PathVariable("articleCodeFk") int articleCodeFk) {
		List<ReplyDTO> replyDTOList = replyService.selectReplyByArticleCodeFk(articleCodeFk);

		// mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		List<ResponseSelectReplyByArticleCodeFk> responseSelectReplyByArticleCodeFkList = new ArrayList<>();

		if (replyDTOList != null) {
			responseSelectReplyByArticleCodeFkList = replyDTOList
				.stream()
				.peek(replyDTO -> replyDTO.setUserDTO(userService.selectUserByUserCodePk(replyDTO.getUserCodeFk())))
				.map(ReplyDTO -> mapper.map(ReplyDTO, ResponseSelectReplyByArticleCodeFk.class))
				.toList();
		}

		return ResponseEntity.status(HttpStatus.OK).body(responseSelectReplyByArticleCodeFkList);
	}

	@GetMapping("reply/user/{userCodeFk}")
	public ResponseEntity<List<ResponseSelectAllReplyByUserCodeFk>> selectAllReplyByUserCodeFk(@PathVariable("userCodeFk") int userCodeFk) {
		List<ReplyDTO> replyDTOList = replyService.selectAllReplyByUserCodeFk(userCodeFk);
		List<ResponseSelectAllReplyByUserCodeFk> responseSelectAllReplyByUserCodeFkList = new ArrayList<>();

		if (replyDTOList != null) {
			responseSelectAllReplyByUserCodeFkList = replyDTOList
				.stream()
				.map(ReplyDTO -> mapper.map(ReplyDTO, ResponseSelectAllReplyByUserCodeFk.class))
				.toList();
		}

		return ResponseEntity.status(HttpStatus.OK).body(responseSelectAllReplyByUserCodeFkList);
	}


}
