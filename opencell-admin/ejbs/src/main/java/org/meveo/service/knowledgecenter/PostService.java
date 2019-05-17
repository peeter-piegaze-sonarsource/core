package org.meveo.service.knowledgecenter;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.model.knowledgeCenter.Post;
import org.meveo.service.base.BusinessService;

@Stateless
public class PostService extends BusinessService<Post> {
	@Override
	public void create(Post post) throws BusinessException{
		if(post.getContent() == null || post.getContent().isEmpty()) {
            throw new ValidationException("Content must not be null or empty");
		}
		if(post.getCollection() == null) {
            throw new ValidationException("Post must belong to a collection");
		}
		super.create(post);
	}
	
	@Override
	public Post update(Post post) throws BusinessException{
		if(post.getContent() == null || post.getContent().isEmpty()) {
            throw new ValidationException("Content must not be null or empty");
		}
		if(post.getCollection() == null) {
            throw new ValidationException("Post must belong to a collection");
		}
		return super.update(post);
	}
}
