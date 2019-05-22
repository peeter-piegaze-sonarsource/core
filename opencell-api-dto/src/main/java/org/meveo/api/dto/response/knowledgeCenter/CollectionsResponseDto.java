package org.meveo.api.dto.response.knowledgeCenter;

import org.meveo.api.dto.knowledgeCenter.CollectionsDto;
import org.meveo.api.dto.response.SearchResponse;

public class CollectionsResponseDto extends SearchResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6000904980733279931L;

	private CollectionsDto collections = new CollectionsDto();

	public CollectionsDto getCollections() {
		return collections;
	}

	public void setCollections(CollectionsDto collections) {
		this.collections = collections;
	}
	
	public String toString() {
        return "CollectionsResponseDto [collections=" + collections + ", toString()=" + super.toString() + "]";
	}
}
