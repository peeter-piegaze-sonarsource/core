package org.meveo.api.codegen;

import java.io.IOException;

import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.service.codegen.DbModelDocsService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DbModelDocsApi extends BaseApi {
	@Inject
	DbModelDocsService dbModelDocsService;
	
	public String generateDocs(String json) {
		ObjectMapper mapper = new ObjectMapper();
		String[] args = new String[3];
        try {
			JsonNode root = mapper.readTree(json);
			args[0] = root.get("inputFolder").asText();
			args[1] = root.get("resourcesFolder").asText();
			args[2] = root.get("outputFolder").asText();
			args[3] = root.get("dbUrl").asText();
			args[4] = root.get("dbId").asText();
			args[5] = root.get("dbPwd").asText();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dbModelDocsService.generateDoc(args);
	}
}
