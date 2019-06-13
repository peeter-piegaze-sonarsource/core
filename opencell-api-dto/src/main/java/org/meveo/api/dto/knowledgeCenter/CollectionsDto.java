package org.meveo.api.dto.knowledgeCenter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CollectionsDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6204340141559016175L;
	
	private List<CollectionDto> collection;
	
	private Long totalNumberOfRecords;

	public CollectionsDto() {

	}
	
	public List<CollectionDto> getCollection(){
		if(collection == null) {
			collection = new ArrayList<CollectionDto>();
		}
		
		return collection;
	}


	public void setCollection(List<CollectionDto> collection) {
		this.collection = collection;
	}
	
	public Long getTotalNumberOfRecords() {
		return totalNumberOfRecords;
	}

	public void setTotalNumberOfRecords(Long totalNumberOfRecords) {
		this.totalNumberOfRecords = totalNumberOfRecords;
	}

	public String toString() {
		return "CollectionsDto [collection=" + collection + "]";
	}
}
