package org.meveo.api.rest.impl;

import javax.inject.Inject;

import org.meveo.api.codegen.DbModelDocsApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.rest.codegen.DbModelDocsRs;

public class DbModelDocsRsImpl extends BaseRs implements DbModelDocsRs {
	@Inject
	DbModelDocsApi dbModelDocsApi;
	
	@Override
	public ActionStatus getDbModelDoc() {
		ActionStatus result = new ActionStatus();
		try {
			result.setMessage(dbModelDocsApi.generateDocs(null));;
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}

}
