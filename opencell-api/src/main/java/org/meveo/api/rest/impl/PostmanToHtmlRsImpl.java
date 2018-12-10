package org.meveo.api.rest.impl;

import javax.inject.Inject;

import org.meveo.api.codegen.PostmanToHtmlApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.rest.codegen.PostmanToHtmlRs;

public class PostmanToHtmlRsImpl extends BaseRs implements PostmanToHtmlRs {
	@Inject
	PostmanToHtmlApi postmanToHtmlApi;
	
	@Override
	public String getPostmanToHtml(String args) {
		ActionStatus result = new ActionStatus();
		try {
			result.setMessage(postmanToHtmlApi.generateDocs(args));;
		} catch (Exception e) {
			processException(e, result);
		}
		if(result.getStatus().equals(ActionStatusEnum.SUCCESS)) {
			return result.getMessage();
		}
		else return result.toString();
	}

}
