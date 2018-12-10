package org.meveo.service.codegen;

import javax.ejb.Stateless;

import org.meveo.model.codegen.PostmanToHtml;


@Stateless
public class PostmanToHtmlService  {
 public String generateDoc(String jsonText) {
		PostmanToHtml postmanToHtml = new PostmanToHtml();
		
		return postmanToHtml.generateModelDoc(jsonText);
	}
}
