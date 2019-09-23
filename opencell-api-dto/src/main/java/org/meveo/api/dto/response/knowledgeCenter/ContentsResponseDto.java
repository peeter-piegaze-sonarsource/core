package org.meveo.api.dto.response.knowledgeCenter;

import org.meveo.api.dto.knowledgeCenter.ContentsDto;
import org.meveo.api.dto.response.SearchResponse;

public class ContentsResponseDto  extends SearchResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2418947932953056974L;

	private ContentsDto contents = new ContentsDto();

	public ContentsDto getContents() {
		return contents;
	}

	public void setContents(ContentsDto contents) {
		this.contents = contents;
	}

	@Override
	public String toString() {
		return "ContentsResponseDto []";
	}
}
