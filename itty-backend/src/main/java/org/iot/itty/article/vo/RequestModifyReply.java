package org.iot.itty.article.vo;

import lombok.Data;

@Data
public class RequestModifyReply {
	private String replyContent;
	private int userCodeFk;
	private int articleCodeFk;
}
