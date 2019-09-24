package org.meveo.api.dto.response;

import org.meveo.api.dto.LanguagesDto;

public class LanguagesResponseDto extends SearchResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4235307326654846240L;
	private LanguagesDto languages = new LanguagesDto();
	
	public LanguagesDto getLanguages() {
		return languages;
	}



	public void setLanguages(LanguagesDto languages) {
		this.languages = languages;
	}

	@Override
	public String toString() {
		return "LanguagesResponseDto [languages=" + languages + "]";
	}
	
	
}
