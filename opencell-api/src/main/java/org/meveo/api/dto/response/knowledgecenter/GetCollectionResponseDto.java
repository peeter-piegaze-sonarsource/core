package org.meveo.api.dto.response.knowledgecenter;

import org.meveo.api.dto.knowledgeCenter.CollectionDto;
import org.meveo.api.dto.response.BaseResponse;

public class GetCollectionResponseDto extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private CollectionDto collection;

	public CollectionDto getCollection() {
		return collection;
	}

	public void setCollection(CollectionDto collection) {
		this.collection = collection;
	}
	
	
}
