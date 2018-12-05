package org.meveo.api.codegen;

import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.service.codegen.DbModelDocsService;

public class DbModelDocsApi extends BaseApi {
	@Inject
	DbModelDocsService dbModelDocsService;
	
	public String generateDocs(String args) {
		String[] newArgs = null;
		if(args != null) newArgs = args.split(",");
		return dbModelDocsService.generateDoc(newArgs);
	}
}
