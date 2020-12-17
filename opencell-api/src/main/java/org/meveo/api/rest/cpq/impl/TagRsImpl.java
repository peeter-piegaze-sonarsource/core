package org.meveo.api.rest.cpq.impl;

import java.util.Collections;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.api.cpq.TagApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.TagDto;
import org.meveo.api.dto.cpq.TagTypeDto;
import org.meveo.api.dto.response.cpq.GetProductDtoResponse;
import org.meveo.api.dto.response.cpq.GetTagDtoResponse;
import org.meveo.api.dto.response.cpq.GetTagTypeDtoResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.rest.cpq.TagRs;
import org.meveo.api.rest.impl.BaseRs;

public class TagRsImpl extends BaseRs implements TagRs {

	@Inject
	private TagApi tagApi;
	
	@Override
	public Response createTag(TagDto tagDto) {
      
        GetTagDtoResponse result = new GetTagDtoResponse();
        try {
        	tagApi.create(tagDto);
        	return Response.ok(result).build();
        } catch(MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
        }
	}

	@Override
	public Response updateTag(TagDto tagDto) {
	     GetTagDtoResponse result = new GetTagDtoResponse();
	        try {
	        	tagApi.update(tagDto);
	        	return Response.ok(result).build();
	        } catch(MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
	        }
	}

	@Override
	public Response deleteTag(String codeTag) {
	     GetTagDtoResponse result = new GetTagDtoResponse();
	        try {
	        	tagApi.removeTag(codeTag);
	        	return Response.ok(result).build();
	        } catch(MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
	        }
	}


	@Override
	public Response findByCode(String codeTag) {
		GetTagDtoResponse result = new GetTagDtoResponse();
	    try {
	    	result.setTagDto(tagApi.findTagByCode(codeTag));
	        return Response.ok(result).build();
	    } catch (MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
	    }
	}

	@Override
	public Response createTagType(TagTypeDto tagTypeDto) {
		 GetTagTypeDtoResponse result = new GetTagTypeDtoResponse();
	        try {
	        	tagApi.create(tagTypeDto);
	        	return Response.ok(result).build();
	        } catch(MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
	        }
	}

	@Override
	public Response updateTagType(TagTypeDto tagTypeDto) {
		 GetTagTypeDtoResponse result = new GetTagTypeDtoResponse();
	        try {
	        	tagApi.update(tagTypeDto);
	        	return Response.ok(result).build();
	        } catch(MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
	        }
	}

	@Override
	public Response findTagTypeBycode(String codeTagType) {
		GetTagTypeDtoResponse result = new GetTagTypeDtoResponse();
	    try {
	    	result.setTagTypeDto(tagApi.findTagTypeByCode(codeTagType));
	        return Response.ok(result).build();
	    } catch (MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
	    }
	}
	
	@Override
	public Response deleteTagType(String codeTag) {
		 GetTagTypeDtoResponse result = new GetTagTypeDtoResponse();
	        try {
	        	tagApi.deleteTagType(codeTag);
	        	return Response.ok(result).build();
	        } catch(MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
	        }
	}
}