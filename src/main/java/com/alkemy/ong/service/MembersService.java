package com.alkemy.ong.service;

import java.util.List;

import com.alkemy.ong.dto.MemberDto;
import com.alkemy.ong.dto.MembersRequest;
import com.alkemy.ong.dto.PageDto;
import org.springframework.web.util.UriComponentsBuilder;

public interface MembersService {	
		
	List<MemberDto> getAllMemberDTOs() throws Exception;
	
	public String createMember(MembersRequest memberRequest);
	
	public Object updateMember(long id, MembersRequest memberRequest);
	
	String delete(Long id) throws Exception;

	PageDto<MemberDto> getMembersByPage(int page, UriComponentsBuilder uriBuilder);
}
