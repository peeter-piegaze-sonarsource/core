package org.meveo.model.codegen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PostmanToHtml {
	public static void main(String[] args) {
		DocGeneratorService docGeneratorService = new DocGeneratorService();
		docGeneratorService.JsonFileToHtmlFile(args[0], args[1]);
	}
	
	public String generateModelDoc(String jsonText) {
		DocGeneratorService docGeneratorService = new DocGeneratorService();
		return docGeneratorService.JsonTextToHtmlText(jsonText);
	}
	
	private static class Doc {
		private String name;
	    private String description;
	    private ArrayList<DocObject> docObjects;
	    private String uuid;

	    public Doc() {
	        docObjects = new ArrayList();
	        uuid = UUID.randomUUID().toString();
	    }

	    
	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    public String getDescription() {
	        return description;
	    }

	    public void setDescription(String description) {
	        this.description = description;
	    }

	    public ArrayList<DocObject> getDocObject() {
	        return docObjects;
	    }

	    public void setDocObject(ArrayList<DocObject> docObject) {
	        this.docObjects = docObject;
	    }

	    public String getUuid() {
	        return uuid;
	    }
	    
	    
	   public String toHtml() {
		   String html = "<html>";
		   List<Extension> extensions = Arrays.asList(TablesExtension.create());
	        Parser parser = Parser.builder()
	        .extensions(extensions)
	        .build();
	        HtmlRenderer renderer = HtmlRenderer.builder()
	        .extensions(extensions)
	        .build();
	        
		   html += Resources.getHead(name);
		   
		   html += "\t<body class=\"pm-main-body\">\n"
				   + "\t\t<main>"
				   + "\t\t\t<div class=\"collection\">\n"
				   + "\t\t\t\t<div class=\"docs-nav\">\n"
				   + "\t\t\t\t\t<ul class=\"docs-nav__items\">\n"
				   + "\t\t\t\t\t<li><ul><li class=\"docs-nav__item docs-nav__item--tocItem\"><div class=\"docs-nav__head\"><div class=\"docs-nav__icon docs-nav__icon--tocItem\"></div><div class=\"docs-nav__name docs-nav__name--tocItem\"><a class=\"docs-nav__link no-select\" href=\"#introduction\" title=\"Introduction\">Introduction</a></div></div></li></ul></li>\n";
		   
		   for(DocObject docObject : docObjects) {
			   html += docObject.toNavHtml();
		   }
		   
		   html += "\t\t\t\t\t</ul>\n"
				   + "\t\t\t\t</div>\n"
				   + "\t\t\t\t<div class=\"docs-body docs-body--double-col\">\n"
				   + "<div class=\"docs-item\" "
				   + "id=\""+ uuid +"\">"
				   + "<div class=\"docs-desc\">"
				   + "<h1 class=\"pm-h1 docs-desc-title docs-desc-title--head\" "
				   + "id=\"introduction\">"
				   + name
				   + "</h1>"
				   + "<div class=\"docs-desc-body pm-markdown\">"
				   + "</div>"
				   + renderer.render(parser.parse(description))
				   + "</div>"
				   + "</div>";
		   
		   for(DocObject docObject : docObjects) {
			   html += docObject.toDocHtml();
		   }
		   
		   html += "\t\t\t\t</div>\n"
				   + "\t\t\t</div>\n"
				   + "\t\t</main>\n";
		   
		   html += Resources.getScripts();
		   
		   html += "</html>";
		   
		   return html;
	   }
	}
	
	
	private static class DocObject {
	    private String name;
	    private String description;
	    private String uuid;
	    private Request request;
	    private ArrayList<DocObject> docObjects;

	    public DocObject() {
	        docObjects = new ArrayList();
	        uuid = UUID.randomUUID().toString();
	    }

	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    public String getDescription() {
	        return description;
	    }

	    public void setDescription(String description) {
	        this.description = description;
	    }

	    public Request getRequest() {
	        return request;
	    }

	    public void setRequest(Request requests) {
	        this.request = requests;
	    }

	    public String getUuid() {
	        return uuid;
	    }

	    public ArrayList<DocObject> getDocObjects() {
	        return docObjects;
	    }

	    public void setDocObjects(ArrayList<DocObject> docObjects) {
	        this.docObjects = docObjects;
	    }
	 
	    
	    public String toNavHtml() {
	    	String html = "";
	    	if(request != null) {
	    		html += "<li class=\"docs-nav__item docs-nav__item--request\">"
                        + "<div class=\"docs-nav__head\">"
                        + "<div class=\"docs-nav__icon docs-nav__icon--request\">"
                        + "<span class=\"docs-nav__method pm-method-color-" + (request.getMethod().contains("DEL") ? "DEL" : request.getMethod()) +"\">"
                        + (request.getMethod().contains("DEL") ? "DEL" : request.getMethod())
                        + "</span>"
                        + "</div>"
                        + "<div class=\"docs-nav__name docs-nav__name--request\">"
                        + "<a class=\"docs-nav__link no-select\" href=\"#"+ uuid +"\" title=\"" + name + "\">"
                        + name
                        + "</a>"
                        + "</div>"
                        + "</div>"
                        + "</li>";
	    	}
	    	else {
	    		html += "<li class=\"docs-nav__item docs-nav__item--folder\">"
                        + "<div class=\"docs-nav__head\">"
                        + "<div class=\"docs-nav__icon docs-nav__icon--folder\">"
                        + "<a class=\"docs-nav__icon--folder\">"
                        + "<i class=\"pm-icon pm-icon-xs pm-icon-secondary rotate-90\">"
                        + "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 18 12\"> "
                        + "<g fill=\"none\" fill-rule=\"evenodd\" transform=\"translate(-23 -27)\"> "
                        + "<rect width=\"64\" height=\"64\" rx=\"5\">"
                        + "</rect> "
                        + "<path fill=\"#535353\" d=\"M32 38.4l-8.533-10.667h17.066\">"
                        + "</path> "
                        + "</g>"
                        + "</svg>"
                        + "</i>"
                        + "</a>"
                        + "</div>"
                        + "<div class=\"docs-nav__name docs-nav__name--folder\">"
                        + "<a class=\"docs-nav__link no-select\" href=\"#"+ uuid +"\" title=\"" + name + "\">"
                        + name
                        + "</a>"
                        + "</div>"
                        + "</div>"
                        + "<ul class=\"docs-nav__content hidden\">";
	    	}
	    	for(DocObject docObject : docObjects) {
		    	html += docObject.toNavHtml(); 
	    	}        
            html += "</ul></li>";
            
            return html;
	    }
	    
	    public String toDocHtml() {
	    	String html = "";
	    	List<Extension> extensions = Arrays.asList(TablesExtension.create());
	        Parser parser = Parser.builder()
	        .extensions(extensions)
	        .build();
	        HtmlRenderer renderer = HtmlRenderer.builder()
	        .extensions(extensions)
	        .build();
	        
	    	if(request != null) {
	    		html += "<div class=\"docs-item\" id=\"" + uuid + "\">"
                        + "<div class=\"docs-desc\">"
                        + "<div>"
                        + "<h2 class=\"pm-h2 docs-desc-title docs-desc-title--request\">"
                                + "<span class=\"pm-method-color-"+ request.getMethod() +"\">"
                                + request.getMethod() +" "
                                + "</span>"
                                + name
                                + (request.getType() == "basic" ?
                                        "<i data-tooltip=\"This request requires authentication\" "
                                        + "class=\"pm-icon pm-icon-sm pm-icon-secondary push-half--left pm-tooltip pm-tooltip--lg docs-desc-title--request-lock\">"
                                        + "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 21 27\"> "
                                        + "<g fill=\"none\" fill-rule=\"evenodd\" transform=\"translate(-22 -19)\"> "
                                        + "<rect width=\"64\" height=\"64\" rx=\"5\">"
                                        + "</rect> "
                                        + "<path fill=\"#535353\" d=\"M32.5 19c-3.867 0-7 3.41-7 7.62v3.808H22v15.24h21v-15.24h-3.5v-3.81c0-4.21-3.133-7.618-7-7.618zm0 3.81c2.082 0 3.5 1.542 3.5 3.81v3.808h-7v-3.81c0-2.266 1.418-3.81 3.5-3.81z\">"
                                        + "</path> "
                                        + "</g>"
                                        + "</svg>"
                                        + "</i>" : ""
                                )
                                + "</h2>"
                                + "<div class=\"docs-desc-title__url\">"
                                + request.getUrl()
                                + "</div>"
                                + "</div>"
                                + "<div class=\"docs-desc-body\">"
                                + "<div class=\"pm-markdown\">"
                                + renderer.render(parser.parse(description))
                                + "</div>"
                                + "<div class=\"docs-request-headers\">"
                                + "<h4 class=\"pm-h4\">"
                                + "Headers"
                                + "</h4>"
                                + "<table class=\"pm-table docs-request-table\">"
                                + "<tbody>"
                                + "<tr>"
                                + "<td class=\"weight--medium\">"
                                + "Content-Type"
                                + "</td>"
                                + "<td>"
                                + request.getContentType()
                                + "<div class=\"pm-markdown docs-request-table__desc\">"
                                + "</div>"
                                + "</td>"
                                + "</tr>"
                                + "<tr>"
                                + "<td class=\"weight--medium\">"
                                + "Authorization"
                                + "</td>"
                                + "<td>"
                                + request.getAuthorization()
                                + "<div class=\"pm-markdown docs-request-table__desc\">"
                                + "</div>"
                                + "</td>"
                                + "</tr>"
                                + "</tbody>"
                                + "</table>"
                                + "</div>"
                                + (request.getRaw().isEmpty() ? "" : ""
                                    + "<div class=\"docs-request-body\">"
                                    + "<h4 class=\"pm-h4 docs-request-body__title\">"
                                    + "Body"
                                    + "</h4>"
                                    + "<span class=\"docs-request-body__mode push-half--left\">"
                                    + "raw (" + request.getContentType() + ")"
                                    + "</span>"
                                    + "<pre class=\"docs-request-body__raw\">"
                                    + "<code>"
                                    + request.getRaw()
                                    + "</code>"
                                    + "</pre>"
                                    + "</div>"  
                                ) 
                                + "</div>"
                                + "</div>"
                                + "<div class=\"docs-example\">"
                                + "<div class=\"docs-example__request\">"
                                + "<div class=\"docs-example__snippet-header\">"
                                + "<span class=\"docs-example__snippet-type\">"
                                + "Example Request"
                                + "</span>"
                                + "<span class=\"docs-example__response-title\" title=\"" + name + "\">"
                                + name
                                + "</span>"
                                + "</div>"
                                + "<div class=\"pm-snippet-container\">"
                                + "<div class=\"pm-snippet pm-snippet-expandable pm-snippet-wrap\">"
                                + "<pre class=\"pm-snippet-body\">"
                                + "<code class=\"hljs curl bash\">"
                                + "curl --request POST \\\n"
                                + "  --url <span class=\"hljs-string\">'"+ request.getUrl() +"'</span> \\\n"
                                + "  --header <span class=\"hljs-string\">'Authorization: " + request.getAuthorization() + "'</span> \\\n"
                                + "  --header <span class=\"hljs-string\">'Content-Type: " + request.getContentType() + "'</span> \\\n"
                                + (request.getRaw().isEmpty() ? "" : 
                                    "  --data "
                                    + "<span class=\"hljs-string\">"
                                    + "'" + request.getRaw() + "'"
                                    + "</span>"
                                )
                                + "</code>"
                                + "</pre>"
                                + "<div class=\"pm-snippet-actions-expand\">"
                                + "<button class=\"pm-btn pm-btn-secondary pm-btn-xs pm-btn-content pm-snippet-expand\">"
                                + "Click to Expand"
                                + "</button>"
                                + "</div>"
                                + "</div>"
                                + "</div>"
                                + "</div>"
                                + "</div>"
                                + "</div>";
	    	}
	    	else {
	    		html += "<div class=\"docs-item\" id=\""+ uuid +"\">"
                        + "<div class=\"docs-desc\">"
                        + "<h2 class=\"pm-h2 docs-desc-title docs-desc-title--folder\">"
                        + name
                        + "</h2>"
                        + "<div class=\"docs-desc-body pm-markdown\">"
                        + "<p>"
                        + renderer.render(parser.parse(description))
                        + "</p>"
                        + "</div>"
                        + "</div>"
                        + "</div>";
	    		for(DocObject docObject : docObjects) {
			    	html += docObject.toDocHtml(); 
		    	} 
	    		
	    	}
	    	
	    	return html;
	    }

	    @Override
	    public String toString() {
	        return "DocObject{" + "name=" + name + ", description=" + description + '}';
	    }
	   
	}
	
	private static class Request {
	    private String type;
	    private String username;
	    private String password;
	    private String method;
	    private String contentType;
	    private String authorization;
	    private String raw;
	    private String url;
	    
	    
	    public String getType() {
	        return type;
	    }

	    public void setType(String type) {
	        this.type = type;
	    }

	    public String getUsername() {
	        return username;
	    }

	    public void setUsername(String username) {
	        this.username = username;
	    }

	    public String getPassword() {
	        return password;
	    }

	    public void setPassword(String password) {
	        this.password = password;
	    }

	    public String getMethod() {
	        return method;
	    }

	    public void setMethod(String method) {
	        this.method = method;
	    }

	    public String getContentType() {
	        return contentType;
	    }

	    public void setContentType(String contentType) {
	        this.contentType = contentType;
	    }

	    public String getAuthorization() {
	        return authorization;
	    }

	    public void setAuthorization(String authorization) {
	        this.authorization = authorization;
	    }

	    public String getRaw() {
	        return raw;
	    }

	    public void setRaw(String raw) {
	        this.raw = raw;
	    }

	    public String getUrl() {
	        return url;
	    }
	    
	    public void setUrl(String url) {
	        this.url = url;
	    }

	}
	
	private static class DocGeneratorService {
	    private File inputFile;
	    private File outputFile;
		private String html = "";	        
	    
	    public void JsonFileToHtmlFile(String inputPath, String outputPath) {
	        try {
	        	BufferedReader br = new BufferedReader(new FileReader(inputPath));
	        	try {
	        	    StringBuilder sb = new StringBuilder();
	        	    String line = br.readLine();

	        	    while (line != null) {
	        	        sb.append(line);
	        	        sb.append(System.lineSeparator());
	        	        line = br.readLine();
	        	    }
	        	    String jsonText = sb.toString();
	        	    JsonTextToHtmlText(jsonText);
	                outHTMLFile(outputPath, html);
	        	} finally {
	        	    br.close();
	        	}
	        } catch (FileNotFoundException ex) {
	            Logger.getLogger(DocGeneratorService.class.getName()).log(Level.SEVERE, null, ex);
	        } catch (IOException ex) {
	            Logger.getLogger(DocGeneratorService.class.getName()).log(Level.SEVERE, null, ex);
	        }

	    }
	    
	    public String JsonTextToHtmlText(String jsonText) {
	    	Doc doc = new Doc(); 	
	    	ObjectMapper mapper = new ObjectMapper();
            JsonNode root;
			try {
				root = mapper.readTree(jsonText);
	            doc.setName(root.path("info").path("name").asText());
	            doc.setDescription(root.path("info").path("description").asText());
	            parseAll(root.path("item"), doc.getDocObject());
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            
            html += doc.toHtml();
			return html;
	    }
	    
	    public void parseAll(JsonNode itemNode, ArrayList<DocObject> docObjects){
	        if(!itemNode.isMissingNode()){
	                for (JsonNode node : itemNode){
	                        DocObject docObject = new DocObject();
	                        docObject.setName(node.path("name").asText());
	                        docObject.setDescription(node.path("description").asText());
	                        
	                        if(!node.path("request").isMissingNode()){
	                            Request request = new Request();
	                            request.setMethod(node.path("request").path("method").asText());
	                            request.setRaw(node.path("request").path("body").path("raw").asText());
	                            request.setUrl(node.path("request").path("url").path("raw").asText());
	                            parseHeader(node.path("request").path("header"), request);
	                            docObject.setRequest(request);
	                        }
	                        
	                        docObjects.add(docObject);
	                        parseAll(node.path("item"), docObject.getDocObjects());
	                }
	        }
	    }
	    
	    public void parseHeader(JsonNode headerNode, Request request){
	        if(!headerNode.isMissingNode()){
	            for (JsonNode node : headerNode){
	                switch(node.path("key").asText()){
	                    case "Content-Type":
	                        request.setContentType(node.path("value").asText());
	                        break;
	                    case "Authorization":
	                        request.setAuthorization(node.path("value").asText());
	                        break;
	                }
	            }
	        }
	    }
	    
	    public void printAll(Doc doc){
	        System.out.println(doc.getName());
	        System.out.println(doc.getDescription());
	        printItems(doc.getDocObject());
	        
	    }
	    
	    public void printItems(ArrayList<DocObject> docObjects){
	        for(DocObject docObject : docObjects){
	            if(docObject.getRequest() != null){
	                System.out.println(docObject.getRequest().getMethod() + " / " + docObject.getName());
	                System.out.println("URL : " + docObject.getRequest().getUrl());
	                System.out.println("Headers");
	                System.out.println("Content-Type\t" + docObject.getRequest().getContentType());
	                System.out.println("Authorization\t" + docObject.getRequest().getAuthorization());
	                System.out.println("Body");
	                System.out.println(docObject.getRequest().getRaw());
	            }
	            else{
	                System.out.println(docObject.getName());
	                System.out.println(docObject.getDescription());
	                printItems(docObject.getDocObjects());
	            }
	        }
	    }
	    
	    public void createHTMLFile(String path, Doc doc){
	        try(FileWriter fw = new FileWriter(path, false);
	            BufferedWriter bw = new BufferedWriter(fw);
	            PrintWriter out = new PrintWriter(bw))
	        {
	            out.println("<html>");
	            outHead(out, doc.getName());
	            outBody(out, doc);
	            out.println("</html>");
	        } catch (IOException e) {
	            //exception handling left as an exercise for the reader
	        }
	        
	    }
	    
	    public void outHTMLFile(String path, String html){
	        try(FileWriter fw = new FileWriter(path, false);
	            BufferedWriter bw = new BufferedWriter(fw);
	            PrintWriter out = new PrintWriter(bw))
	        {
	            out.print(html);
	        } catch (IOException e) {
	        }
	        
	    }
	    
	    public void outHead(PrintWriter out, String name){
	        out.println("<head>\n"
	                + "    <!-- Google Analytics -->\n"
	                + "    <script async=\"\" src=\"https://www.googletagmanager.com/gtag/js?id=UA-43979731-14\"></script>\n"
	                + "    <script>\n"
	                + "      window.dataLayer = window.dataLayer || [];\n"
	                + "      function gtag(){dataLayer.push(arguments);}\n"
	                + "      gtag('js', new Date());\n"
	                + "\n"
	                + "      gtag('config', 'UA-43979731-14');\n"
	                + "    </script>\n"
	                + "    <!-- End Google Analytics -->\n"
	                + "\n"
	                + "    <!-- Intercom -->\n"
	                + "    \n"
	                + "    <!-- End Intercom -->\n"
	                + "\n"
	                + "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=\"utf-8\">\n"
	                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
	                + "    <link rel=\"apple-touch-icon\" href=\"https://web.postman.co/prodash/img/apple-touch-icon.png\">\n"
	                + "    <title>" + name + "</title>\n"
	                + "\n"
	                + "    <style type=\"text/css\">\n"
	                + "      #noscript-content {\n"
	                + "        background: white;\n"
	                + "        padding: 16px 32px;\n"
	                + "        border-radius: 3px;\n"
	                + "        max-width: 600px;\n"
	                + "        margin: 100px auto;\n"
	                + "        color: #282828;\n"
	                + "      }\n"
	                + "\n"
	                + "      #noscript-content p,\n"
	                + "      #noscript-content ul {\n"
	                + "        line-height: 1.7;\n"
	                + "      }\n"
	                + "\n"
	                + "      #noscript-content ul {\n"
	                + "        padding-left: 24px;\n"
	                + "      }\n"
	                + "\n"
	                + "      #noscript-content li {\n"
	                + "        margin: 8px 0;\n"
	                + "      }\n"
	                + "    </style>\n"
	                + "    <link href=\"https://web.postman.co/prodash/css/main.css?v=2.9.0\" rel=\"stylesheet\" type=\"text/css\">\n"
	                + "    <link href=\"https://fonts.googleapis.com/css?family=Open+Sans:300,400,600,700\" rel=\"stylesheet\" type=\"text/css\">\n"
	                + "  <script type=\"text/javascript\" charset=\"utf-8\" async=\"\" src=\"https://web.postman.co/prodash/js/2.838779bb4ade4d60a665.chunk.js\">"
	                + "</script>"
	                + "</head>");
	    }
	    
	    public void outBody(PrintWriter out, Doc doc){
	        List<Extension> extensions = Arrays.asList(TablesExtension.create());
	        Parser parser = Parser.builder()
	        .extensions(extensions)
	        .build();
	        HtmlRenderer renderer = HtmlRenderer.builder()
	        .extensions(extensions)
	        .build();
	        
	        out.println("\t<body class=\"pm-main-body\">");
	        out.println("\t\t<main>");
	        out.println("\t\t\t<div class=\"collection\">");

	        out.println("\t\t\t\t<div class=\"docs-nav\">");
	        out.println("\t\t\t\t\t<ul class=\"docs-nav__items\">");
	        out.println("\t\t\t\t\t<li><ul><li class=\"docs-nav__item docs-nav__item--tocItem\"><div class=\"docs-nav__head\"><div class=\"docs-nav__icon docs-nav__icon--tocItem\"></div><div class=\"docs-nav__name docs-nav__name--tocItem\"><a class=\"docs-nav__link no-select\" href=\"#introduction\" title=\"Introduction\">Introduction</a></div></div></li></ul></li>");
	        
	        outNav(out, doc.getDocObject());
	        
	        out.println("\t\t\t\t\t</ul>");
	        out.println("\t\t\t\t</div>");

	        out.println("\t\t\t\t<div class=\"docs-body docs-body--double-col\">");
	        out.println("<div class=\"docs-item\" "
	                + "id=\""+ doc.getUuid() +"\">"
	                + "<div class=\"docs-desc\">"
	                + "<h1 class=\"pm-h1 docs-desc-title docs-desc-title--head\" "
	                + "id=\"introduction\">"
	                + doc.getName()
	                + "</h1>"
	                + "<div class=\"docs-desc-body pm-markdown\">"
	                + "</div>"
	                + renderer.render(parser.parse(doc.getDescription()))
	                + "</div>"
	                + "</div>");
	        
	        outDoc(out, doc.getDocObject());
	        
	        out.println("\t\t\t\t</div>");


	        out.println("\t\t\t</div>");
	        out.println("\t\t</main>");
	        
	        outScripts(out);
	        
	        out.println("<div class=\"ReactModalPortal\"></div>");
	        out.println("\t</body>");
	    }
	    
	    public void outNav(PrintWriter out, ArrayList<DocObject> docObjects){
	        for(DocObject docObject : docObjects){
	            if(docObject.getRequest() != null){
	                out.print("<li class=\"docs-nav__item docs-nav__item--request\">"
	                        + "<div class=\"docs-nav__head\">"
	                        + "<div class=\"docs-nav__icon docs-nav__icon--request\">"
	                        + "<span class=\"docs-nav__method pm-method-color-" + (docObject.getRequest().getMethod().contains("DEL") ? "DEL" : docObject.getRequest().getMethod()) +"\">"
	                        + (docObject.getRequest().getMethod().contains("DEL") ? "DEL" : docObject.getRequest().getMethod())
	                        + "</span>"
	                        + "</div>"
	                        + "<div class=\"docs-nav__name docs-nav__name--request\">"
	                        + "<a class=\"docs-nav__link no-select\" href=\"#"+ docObject.getUuid() +"\" title=\"" + docObject.getName() + "\">"
	                        + docObject.getName()
	                        + "</a>"
	                        + "</div>"
	                        + "</div>"
	                        + "</li>");
	            }
	            else{
	                out.print("<li class=\"docs-nav__item docs-nav__item--folder\">"
	                        + "<div class=\"docs-nav__head\">"
	                        + "<div class=\"docs-nav__icon docs-nav__icon--folder\">"
	                        + "<a class=\"docs-nav__icon--folder\">"
	                        + "<i class=\"pm-icon pm-icon-xs pm-icon-secondary rotate-90\">"
	                        + "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 18 12\"> "
	                        + "<g fill=\"none\" fill-rule=\"evenodd\" transform=\"translate(-23 -27)\"> "
	                        + "<rect width=\"64\" height=\"64\" rx=\"5\">"
	                        + "</rect> "
	                        + "<path fill=\"#535353\" d=\"M32 38.4l-8.533-10.667h17.066\">"
	                        + "</path> "
	                        + "</g>"
	                        + "</svg>"
	                        + "</i>"
	                        + "</a>"
	                        + "</div>"
	                        + "<div class=\"docs-nav__name docs-nav__name--folder\">"
	                        + "<a class=\"docs-nav__link no-select\" href=\"#"+ docObject.getUuid() +"\" title=\"" + docObject.getName() + "\">"
	                        + docObject.getName()
	                        + "</a>"
	                        + "</div>"
	                        + "</div>"
	                        + "<ul class=\"docs-nav__content hidden\">");
	                outNav(out, docObject.getDocObjects());         
	                out.print("</ul></li>");
	            }
	        }
	    }

	    public void outDoc(PrintWriter out, ArrayList<DocObject> docObjects) {
	        List<Extension> extensions = Arrays.asList(TablesExtension.create());
	        Parser parser = Parser.builder()
	        .extensions(extensions)
	        .build();
	        HtmlRenderer renderer = HtmlRenderer.builder()
	        .extensions(extensions)
	        .build();
	        
	        for(DocObject docObject : docObjects) {
	            if(docObject.getRequest() != null){
	                out.println("<div class=\"docs-item\" id=\"" + docObject.getUuid() + "\">"
	                        + "<div class=\"docs-desc\">"
	                        + "<div>"
	                        + "<h2 class=\"pm-h2 docs-desc-title docs-desc-title--request\">"
	                                + "<span class=\"pm-method-color-"+ docObject.getRequest().getMethod() +"\">"
	                                + docObject.getRequest().getMethod() +" "
	                                + "</span>"
	                                + docObject.getName()
	                                + (docObject.getRequest().getType() == "basic" ?
	                                        "<i data-tooltip=\"This request requires authentication\" "
	                                        + "class=\"pm-icon pm-icon-sm pm-icon-secondary push-half--left pm-tooltip pm-tooltip--lg docs-desc-title--request-lock\">"
	                                        + "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 21 27\"> "
	                                        + "<g fill=\"none\" fill-rule=\"evenodd\" transform=\"translate(-22 -19)\"> "
	                                        + "<rect width=\"64\" height=\"64\" rx=\"5\">"
	                                        + "</rect> "
	                                        + "<path fill=\"#535353\" d=\"M32.5 19c-3.867 0-7 3.41-7 7.62v3.808H22v15.24h21v-15.24h-3.5v-3.81c0-4.21-3.133-7.618-7-7.618zm0 3.81c2.082 0 3.5 1.542 3.5 3.81v3.808h-7v-3.81c0-2.266 1.418-3.81 3.5-3.81z\">"
	                                        + "</path> "
	                                        + "</g>"
	                                        + "</svg>"
	                                        + "</i>" : ""
	                                )
	                                + "</h2>"
	                                + "<div class=\"docs-desc-title__url\">"
	                                + docObject.getRequest().getUrl()
	                                + "</div>"
	                                + "</div>"
	                                + "<div class=\"docs-desc-body\">"
	                                + "<div class=\"pm-markdown\">"
	                                + renderer.render(parser.parse(docObject.getDescription()))
	                                + "</div>"
	                                + "<div class=\"docs-request-headers\">"
	                                + "<h4 class=\"pm-h4\">"
	                                + "Headers"
	                                + "</h4>"
	                                + "<table class=\"pm-table docs-request-table\">"
	                                + "<tbody>"
	                                + "<tr>"
	                                + "<td class=\"weight--medium\">"
	                                + "Content-Type"
	                                + "</td>"
	                                + "<td>"
	                                + docObject.getRequest().getContentType()
	                                + "<div class=\"pm-markdown docs-request-table__desc\">"
	                                + "</div>"
	                                + "</td>"
	                                + "</tr>"
	                                + "<tr>"
	                                + "<td class=\"weight--medium\">"
	                                + "Authorization"
	                                + "</td>"
	                                + "<td>"
	                                + docObject.getRequest().getAuthorization()
	                                + "<div class=\"pm-markdown docs-request-table__desc\">"
	                                + "</div>"
	                                + "</td>"
	                                + "</tr>"
	                                + "</tbody>"
	                                + "</table>"
	                                + "</div>"
	                                + (docObject.getRequest().getRaw().isEmpty() ? "" : ""
	                                    + "<div class=\"docs-request-body\">"
	                                    + "<h4 class=\"pm-h4 docs-request-body__title\">"
	                                    + "Body"
	                                    + "</h4>"
	                                    + "<span class=\"docs-request-body__mode push-half--left\">"
	                                    + "raw (" + docObject.getRequest().getContentType() + ")"
	                                    + "</span>"
	                                    + "<pre class=\"docs-request-body__raw\">"
	                                    + "<code>"
	                                    + docObject.getRequest().getRaw()
	                                    + "</code>"
	                                    + "</pre>"
	                                    + "</div>"  
	                                ) 
	                                + "</div>"
	                                + "</div>"
	                                + "<div class=\"docs-example\">"
	                                + "<div class=\"docs-example__request\">"
	                                + "<div class=\"docs-example__snippet-header\">"
	                                + "<span class=\"docs-example__snippet-type\">"
	                                + "Example Request"
	                                + "</span>"
	                                + "<span class=\"docs-example__response-title\" title=\"" + docObject.getName() + "\">"
	                                + docObject.getName()
	                                + "</span>"
	                                + "</div>"
	                                + "<div class=\"pm-snippet-container\">"
	                                + "<div class=\"pm-snippet pm-snippet-expandable pm-snippet-wrap\">"
	                                + "<pre class=\"pm-snippet-body\">"
	                                + "<code class=\"hljs curl bash\">"
	                                + "curl --request POST \\\n"
	                                + "  --url <span class=\"hljs-string\">'"+ docObject.getRequest().getUrl() +"'</span> \\\n"
	                                + "  --header <span class=\"hljs-string\">'Authorization: " + docObject.getRequest().getAuthorization() + "'</span> \\\n"
	                                + "  --header <span class=\"hljs-string\">'Content-Type: " + docObject.getRequest().getContentType() + "'</span> \\\n"
	                                + (docObject.getRequest().getRaw().isEmpty() ? "" : 
	                                    "  --data "
	                                    + "<span class=\"hljs-string\">"
	                                    + "'" + docObject.getRequest().getRaw() + "'"
	                                    + "</span>"
	                                )
	                                + "</code>"
	                                + "</pre>"
	                                + "<div class=\"pm-snippet-actions-expand\">"
	                                + "<button class=\"pm-btn pm-btn-secondary pm-btn-xs pm-btn-content pm-snippet-expand\">"
	                                + "Click to Expand"
	                                + "</button>"
	                                + "</div>"
	                                + "</div>"
	                                + "</div>"
	                                + "</div>"
	                                + "</div>"
	                                + "</div>");
	            }
	            else{
	                out.println("<div class=\"docs-item\" id=\""+ docObject.getUuid() +"\">"
	                        + "<div class=\"docs-desc\">"
	                        + "<h2 class=\"pm-h2 docs-desc-title docs-desc-title--folder\">"
	                        + docObject.getName()
	                        + "</h2>"
	                        + "<div class=\"docs-desc-body pm-markdown\">"
	                        + "<p>"
	                        + renderer.render(parser.parse(docObject.getDescription()))
	                        + "</p>"
	                        + "</div>"
	                        + "</div>"
	                        + "</div>");
	                outDoc(out, docObject.getDocObjects());
	            }
	        }
	    }

	    public void outScripts(PrintWriter out) {
	        /*
	        out.println("<script src=\"https://cdn.ravenjs.com/3.17.0/raven.min.js\" crossorigin=\"anonymous\"></script>");
	        out.println("<script src=\"https://web.postman.co/prodash/js/manifest.0fb67c5adda4f45daefb.js\"></script>");
	        out.println("<script src=\"https://web.postman.co/prodash/js/vendor.31a174026fdd93acbd12.js\"></script>");
	        out.println("<script src=\"https://web.postman.co/prodash/js/app.1aa861d11db6833f089a.js\" id=\"script-data-scope\" data-user-id=\"4494829\" data-team-id=\"0\"></script>");
	        */
	        out.println("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js\"></script>");
	        out.println("<script>"
	                + "$('.docs-nav__link').click(function(){\n"
	                + "        $(this).parent().parent().siblings(\".docs-nav__content\").toggleClass('hidden');\n"
	                + "        $(this).parent().siblings(\".docs-nav__icon\").children(\"a\").children(\"i\").toggleClass('rotate-90');\n"
	                + "});\n\n"
	                
	                + "$('.docs-nav__icon--folder').click(function(){\n"
	                + "        $(this).parent().parent().siblings(\".docs-nav__content\").toggleClass('hidden');\n"
	                + "        $(this).children(\"a\").children(\"i\").toggleClass('rotate-90');\n"
	                + "});\n\n"
	                
	                + "$('button.pm-snippet-expand').click(function(){\n"
	                + "$('div.ReactModalPortal').html('');\n"
	                + "$('body').toggleClass('ReactModal__Body--open');\n"
	                + "var txt = \"<div class=\\\"ReactModal__Overlay ReactModal__Overlay--after-open pm-modal-background\\\" aria-modal=\\\"true\\\"><div class=\\\"ReactModal__Content ReactModal__Content--after-open pm-modal\\\" tabindex=\\\"-1\\\" aria-label=\\\"pm-modal\\\"><div class=\\\"pm-snippet-modal\\\"><div class=\\\"pm-snippet-modal-header\\\"><div class=\\\"pm-snippet-modal-title\\\">\" + $(this).parent().parent().parent().siblings(\".docs-example__snippet-header\").children(\"span.docs-example__response-title\").text() + \"</div><button class=\\\"pm-btn pm-btn-alternate pm-btn-sm pm-btn-icon pm-snippet-modal-close\\\"><i class=\\\"pm-icon pm-icon-sm pm-icon-secondary\\\"><svg width=\\\"26\\\" height=\\\"26\\\" viewBox=\\\"0 0 26 26\\\" xmlns=\\\"http://www.w3.org/2000/svg\\\"><path d=\\\"M16.064 13l9.098-9.097A2.169 2.169 0 0 0 25.16.84a2.16 2.16 0 0 0-3.063-.002L13 9.936 3.903.838A2.169 2.169 0 0 0 .84.84a2.16 2.16 0 0 0-.002 3.063L9.936 13 .838 22.097A2.169 2.169 0 0 0 .84 25.16a2.16 2.16 0 0 0 3.063.002L13 16.064l9.097 9.098a2.169 2.169 0 0 0 3.063-.002 2.16 2.16 0 0 0 .002-3.063L16.064 13z\\\" fill=\\\"#535353\\\" fill-rule=\\\"evenodd\\\"></path></svg></i></button></div><div class=\\\"pm-snippet-modal-body\\\"><div class=\\\"pm-snippet pm-snippet-wrap\\\"><pre class=\\\"pm-snippet-body\\\"></pre></div></div></div></div></div>\";\n"
	                + "$('div.ReactModalPortal').append(txt);\n"
	                + "$(this).parent().siblings(\"pre.pm-snippet-body\").children(\"code\").clone().appendTo(\"div.ReactModalPortal pre.pm-snippet-body\");\n"
	                + "});\n\n"
	                
	                + "$(document).on('click', 'button.pm-snippet-modal-close', function(){"
	                + "$('body').toggleClass('ReactModal__Body--open');\n"
	                + "$('div.ReactModalPortal').html('');\n"
	                + "});\n\n"
	                                
	                + "</script>");
	    
	    }
	}
	
	private static class Resources {
		private static String getHead(String name) {
			return "<head>\n"
	                + "    <!-- Google Analytics -->\n"
	                + "    <script async=\"\" src=\"https://www.googletagmanager.com/gtag/js?id=UA-43979731-14\"></script>\n"
	                + "    <script>\n"
	                + "      window.dataLayer = window.dataLayer || [];\n"
	                + "      function gtag(){dataLayer.push(arguments);}\n"
	                + "      gtag('js', new Date());\n"
	                + "\n"
	                + "      gtag('config', 'UA-43979731-14');\n"
	                + "    </script>\n"
	                + "    <!-- End Google Analytics -->\n"
	                + "\n"
	                + "    <!-- Intercom -->\n"
	                + "    \n"
	                + "    <!-- End Intercom -->\n"
	                + "\n"
	                + "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=\"utf-8\">\n"
	                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
	                + "    <link rel=\"apple-touch-icon\" href=\"https://web.postman.co/prodash/img/apple-touch-icon.png\">\n"
	                + "    <title>" + name + "</title>\n"
	                + "\n"
	                + "    <style type=\"text/css\">\n"
	                + "      #noscript-content {\n"
	                + "        background: white;\n"
	                + "        padding: 16px 32px;\n"
	                + "        border-radius: 3px;\n"
	                + "        max-width: 600px;\n"
	                + "        margin: 100px auto;\n"
	                + "        color: #282828;\n"
	                + "      }\n"
	                + "\n"
	                + "      #noscript-content p,\n"
	                + "      #noscript-content ul {\n"
	                + "        line-height: 1.7;\n"
	                + "      }\n"
	                + "\n"
	                + "      #noscript-content ul {\n"
	                + "        padding-left: 24px;\n"
	                + "      }\n"
	                + "\n"
	                + "      #noscript-content li {\n"
	                + "        margin: 8px 0;\n"
	                + "      }\n"
	                + "    </style>\n"
	                + "    <link href=\"https://web.postman.co/prodash/css/main.css?v=2.9.0\" rel=\"stylesheet\" type=\"text/css\">\n"
	                + "    <link href=\"https://fonts.googleapis.com/css?family=Open+Sans:300,400,600,700\" rel=\"stylesheet\" type=\"text/css\">\n"
	                + "  <script type=\"text/javascript\" charset=\"utf-8\" async=\"\" src=\"https://web.postman.co/prodash/js/2.838779bb4ade4d60a665.chunk.js\">"
	                + "</script>"
	                + "</head>";
		}

		public static String getScripts() {
			return "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js\"></script>"
					+ "<script>"
	                + "$('.docs-nav__link').click(function(){\n"
	                + "        $(this).parent().parent().siblings(\".docs-nav__content\").toggleClass('hidden');\n"
	                + "        $(this).parent().siblings(\".docs-nav__icon\").children(\"a\").children(\"i\").toggleClass('rotate-90');\n"
	                + "});\n\n"
	                
	                + "$('.docs-nav__icon--folder').click(function(){\n"
	                + "        $(this).parent().parent().siblings(\".docs-nav__content\").toggleClass('hidden');\n"
	                + "        $(this).children(\"a\").children(\"i\").toggleClass('rotate-90');\n"
	                + "});\n\n"
	                
	                + "$('button.pm-snippet-expand').click(function(){\n"
	                + "$('div.ReactModalPortal').html('');\n"
	                + "$('body').toggleClass('ReactModal__Body--open');\n"
	                + "var txt = \"<div class=\\\"ReactModal__Overlay ReactModal__Overlay--after-open pm-modal-background\\\" aria-modal=\\\"true\\\"><div class=\\\"ReactModal__Content ReactModal__Content--after-open pm-modal\\\" tabindex=\\\"-1\\\" aria-label=\\\"pm-modal\\\"><div class=\\\"pm-snippet-modal\\\"><div class=\\\"pm-snippet-modal-header\\\"><div class=\\\"pm-snippet-modal-title\\\">\" + $(this).parent().parent().parent().siblings(\".docs-example__snippet-header\").children(\"span.docs-example__response-title\").text() + \"</div><button class=\\\"pm-btn pm-btn-alternate pm-btn-sm pm-btn-icon pm-snippet-modal-close\\\"><i class=\\\"pm-icon pm-icon-sm pm-icon-secondary\\\"><svg width=\\\"26\\\" height=\\\"26\\\" viewBox=\\\"0 0 26 26\\\" xmlns=\\\"http://www.w3.org/2000/svg\\\"><path d=\\\"M16.064 13l9.098-9.097A2.169 2.169 0 0 0 25.16.84a2.16 2.16 0 0 0-3.063-.002L13 9.936 3.903.838A2.169 2.169 0 0 0 .84.84a2.16 2.16 0 0 0-.002 3.063L9.936 13 .838 22.097A2.169 2.169 0 0 0 .84 25.16a2.16 2.16 0 0 0 3.063.002L13 16.064l9.097 9.098a2.169 2.169 0 0 0 3.063-.002 2.16 2.16 0 0 0 .002-3.063L16.064 13z\\\" fill=\\\"#535353\\\" fill-rule=\\\"evenodd\\\"></path></svg></i></button></div><div class=\\\"pm-snippet-modal-body\\\"><div class=\\\"pm-snippet pm-snippet-wrap\\\"><pre class=\\\"pm-snippet-body\\\"></pre></div></div></div></div></div>\";\n"
	                + "$('div.ReactModalPortal').append(txt);\n"
	                + "$(this).parent().siblings(\"pre.pm-snippet-body\").children(\"code\").clone().appendTo(\"div.ReactModalPortal pre.pm-snippet-body\");\n"
	                + "});\n\n"
	                
	                + "$(document).on('click', 'button.pm-snippet-modal-close', function(){"
	                + "$('body').toggleClass('ReactModal__Body--open');\n"
	                + "$('div.ReactModalPortal').html('');\n"
	                + "});\n\n"
	                                
	                + "</script>";
		}
	}

	
}
