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
		return dbModelDocs.generateModelDoc(args);
	}
}
