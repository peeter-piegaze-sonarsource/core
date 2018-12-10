package org.meveo.api.codegen;

import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.service.codegen.PostmanToHtmlService;

public class PostmanToHtmlApi extends BaseApi {
	@Inject
	PostmanToHtmlService postmanToHtmlService;
	
	public String generateDocs(String jsonText) {
		return postmanToHtmlService.generateDoc(jsonText);
	}
}
