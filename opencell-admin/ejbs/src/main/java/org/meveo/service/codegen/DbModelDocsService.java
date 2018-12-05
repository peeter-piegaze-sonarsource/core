package org.meveo.service.codegen;

import javax.ejb.Stateless;

import org.meveo.model.codegen.DbModelDocs;


@Stateless
public class DbModelDocsService  {
    public static void main(String[] args) {
		DbModelDocsService dbModelDocsService = new DbModelDocsService();
    	dbModelDocsService.generateDoc(null);
    }
    
	public String generateDoc(String[] args) {
		DbModelDocs dbModelDocs = new DbModelDocs();
		String[] defaultArg = {"Y:\\Opencell\\Sources\\Community\\opencell-model\\src\\main\\java",
				"Y:\\Opencell\\Sources\\Community\\opencell-model\\src\\main\\resources\\dbModelRessources",
				"Y:\\Opencell\\Sources\\Community\\opencell-model\\target/docs/dbModel"};
		
		return dbModelDocs.generateModelDoc((args != null ? args : defaultArg));
	}
}
