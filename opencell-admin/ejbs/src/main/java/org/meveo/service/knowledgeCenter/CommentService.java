package org.meveo.service.knowledgeCenter;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.model.knowledgeCenter.Comment;
import org.meveo.service.base.BusinessService;

public class CommentService extends BusinessService<Comment>{

	@Override
	public void create(Comment comment) throws BusinessException{
		if(comment.getMarkdownContent() == null) {
            throw new ValidationException("MarkdownContent must not be null or empty");
		}
		
		super.create(comment);
	}
	
	@Override
	public Comment update(Comment comment) throws BusinessException{
		if(comment.getMarkdownContent() == null) {
            throw new ValidationException("MarkdownContent must not be null or empty");
		}
		
		return super.update(comment);
	}
}
