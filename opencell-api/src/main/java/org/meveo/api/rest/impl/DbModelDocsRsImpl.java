package org.meveo.api.rest.impl;

import javax.inject.Inject;

import org.meveo.api.codegen.DbModelDocsApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.rest.codegen.DbModelDocsRs;

public class DbModelDocsRsImpl extends BaseRs implements DbModelDocsRs {
	@Inject
	DbModelDocsApi dbModelDocsApi;
	
	@Override
	public String getDbModelDoc(String args) {
		ActionStatus result = new ActionStatus();
		try {
			result.setMessage(dbModelDocsApi.generateDocs(args));;
		} catch (Exception e) {
			processException(e, result);
		}
		if(result.getStatus().equals(ActionStatusEnum.SUCCESS)) {
			return result.getMessage();
		}
		else return result.toString();
	}

}
