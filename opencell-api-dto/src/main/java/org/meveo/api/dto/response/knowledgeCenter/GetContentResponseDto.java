package org.meveo.api.dto.response.knowledgeCenter;

import org.meveo.api.dto.knowledgeCenter.ContentDto;
import org.meveo.api.dto.response.BaseResponse;

public class GetContentResponseDto extends BaseResponse {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8954164967830008589L;
	
	private ContentDto content;

	public ContentDto getContent() {
		return content;
	}

	public void setContent(ContentDto content) {
		this.content = content;
	}
	
	
}
