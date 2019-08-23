package org.meveo.service.knowledgeCenter;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.model.knowledgeCenter.Collection;
import org.meveo.service.base.BusinessService;

@Stateless
public class CollectionService  extends BusinessService<Collection> {
	@Override
	public void create(Collection collection) throws BusinessException{
		if(collection.getMarkdownContents() == null || collection.getMarkdownContents().isEmpty()) {
            throw new ValidationException("MarkdownContent must not be null or empty");
		}
		if(collection.getCode() == null || collection.getCode().isEmpty()) {
            throw new ValidationException("Code must not be null or empty");
		}
		super.create(collection);
	}
	
	@Override
	public Collection update(Collection collection) throws BusinessException{
		if(collection.getMarkdownContents() == null || collection.getMarkdownContents().isEmpty()) {
            throw new ValidationException("MarkdownContent must not be null or empty");
		}
		if(collection.getCode() == null || collection.getCode().isEmpty()) {
            throw new ValidationException("Code must not be null or empty");
		}
		return super.update(collection);
	}
}
