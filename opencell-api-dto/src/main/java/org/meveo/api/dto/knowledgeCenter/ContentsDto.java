package org.meveo.api.dto.knowledgeCenter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContentsDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7451998834776194900L;
	
	private List<ContentDto> contents;
	
	private Long totalNumberOfRecords;

	public ContentsDto() {

	}

	public List<ContentDto> getContents() {
		if(contents == null) {
			contents = new ArrayList<ContentDto>();
		}
		return contents;
	}

	public void setContents(List<ContentDto> contents) {
		this.contents = contents;
	}

	public Long getTotalNumberOfRecords() {
		return totalNumberOfRecords;
	}

	public void setTotalNumberOfRecords(Long totalNumberOfRecords) {
		this.totalNumberOfRecords = totalNumberOfRecords;
	}

	@Override
	public String toString() {
		return "ContentsDto [contents=" + contents + ", totalNumberOfRecords=" + totalNumberOfRecords + "]";
	}
}
