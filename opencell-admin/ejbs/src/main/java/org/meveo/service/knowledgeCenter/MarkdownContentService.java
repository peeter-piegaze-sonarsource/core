package org.meveo.service.knowledgeCenter;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.model.knowledgeCenter.MarkdownContent;
import org.meveo.service.base.BusinessService;

@Stateless

public class MarkdownContentService extends BusinessService <MarkdownContent> {
	@Override
	public void create(MarkdownContent markdownContent) throws BusinessException{
		if(markdownContent.getContent() == null || markdownContent.getContent().isEmpty()) {
            throw new ValidationException("MarkdownContent must have a content");
		}
		if(markdownContent.getName() == null) {
            throw new ValidationException("MarkdownContent must have a name (title)");
		}
		if(markdownContent.getLanguage() == null) {
            throw new ValidationException("MarkdownContent must have a language");
		}
		super.create(markdownContent);
	}
	
	@Override
	public MarkdownContent update(MarkdownContent markdownContent) throws BusinessException{
		if(markdownContent.getContent() == null || markdownContent.getContent().isEmpty()) {
            throw new ValidationException("MarkdownContent must have a content");
		}
		if(markdownContent.getName() == null) {
            throw new ValidationException("MarkdownContent must have a name (title)");
		}
		if(markdownContent.getLanguage() == null) {
            throw new ValidationException("MarkdownContent must have a language");
		}
		return super.update(markdownContent);
	}
}
