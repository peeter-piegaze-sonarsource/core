package org.meveo.api.dto.knowledgeCenter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContentsDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7451998834776194900L;
	
	private List<ContentDto> content;
	
	private Long totalNumberOfRecords;

	public ContentsDto() {

	}

	public List<ContentDto> getContent() {
		if(content == null) {
			content = new ArrayList<ContentDto>();
		}
		return content;
	}

	public void setContent(List<ContentDto> content) {
		this.content = content;
	}

	public Long getTotalNumberOfRecords() {
		return totalNumberOfRecords;
	}

	public void setTotalNumberOfRecords(Long totalNumberOfRecords) {
		this.totalNumberOfRecords = totalNumberOfRecords;
	}

	@Override
	public String toString() {
		return "ContentsDto [contents=" + content + ", totalNumberOfRecords=" + totalNumberOfRecords + "]";
	}
}
