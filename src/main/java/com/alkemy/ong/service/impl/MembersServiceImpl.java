package com.alkemy.ong.service.impl;

import java.util.List;

import com.alkemy.ong.dto.MemberDto;

import java.util.Locale;

import com.alkemy.ong.dto.MembersRequest;
import com.alkemy.ong.dto.PageDto;
import com.alkemy.ong.exception.EntityNotFoundException;
import com.alkemy.ong.model.Members;
import com.alkemy.ong.repository.MembersRepository;
import com.alkemy.ong.service.MembersService;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;


@RequiredArgsConstructor
@Service
public class MembersServiceImpl implements MembersService {

	private final MembersRepository memberRepository;
	private final MessageSource messageSource;
	private final AmazonClient amazonClient;

	private static final int PAGE_SIZE = 10;

	@Override
	public String createMember(MembersRequest memberRequest) {
		Members member = new Members();

		member.setName(memberRequest.name);
		member.setFacebookUrl(memberRequest.facebookUrl);
		member.setInstagramUrl(memberRequest.instagramUrl);
		member.setLinkedinUrl(memberRequest.linkedinUrl);
		member.setImage(amazonClient.uploadFile(memberRequest.getImage()));
		member.setDescription(memberRequest.description);

		memberRepository.save(member);
		return messageSource.getMessage("member.created", new Object[] { "Member" }, Locale.US);
	}

	@Override
	public MemberDto updateMember(long id, MembersRequest memberRequest) {
		Members memberToUpdate = memberRepository.findById(id).orElseThrow(this::makeEntityNotFoundException);
		if (memberRequest.name != null) {
			memberToUpdate.setName(memberRequest.name);
		}
		if (memberRequest.facebookUrl != null) {
			memberToUpdate.setFacebookUrl(memberRequest.facebookUrl);
		}
		if (memberRequest.instagramUrl != null) {
			memberToUpdate.setInstagramUrl(memberRequest.instagramUrl);
		}
		if (memberRequest.linkedinUrl != null) {
			memberToUpdate.setLinkedinUrl(memberRequest.linkedinUrl);
		}
		if (memberRequest.image != null) {
			amazonClient.deleteFileFromS3Bucket(memberToUpdate.getImage());
			memberToUpdate.setImage(amazonClient.uploadFile(memberRequest.image));
		}
		if (memberRequest.description != null) {
			memberToUpdate.setDescription(memberRequest.description);
		}
		memberRepository.save(memberToUpdate);
		return mapToDto(memberToUpdate);
	}

	private MemberDto mapToDto(Members member) {
		return MemberDto.builder().name(member.getName())
				.facebookUrl(member.getFacebookUrl())
				.instagramUrl(member.getInstagramUrl())
				.linkedinUrl(member.getLinkedinUrl())
				.image(member.getImage())
				.description(member.getDescription())
				.build();
	}

	private EntityNotFoundException makeEntityNotFoundException() {
		String error = messageSource.getMessage("error.entity.not-found", new Object[] { "Member" }, Locale.US);
		return new EntityNotFoundException(error);
	}

	@Override
    public String delete(Long id) throws Exception {
        String message = "";
        try {
            memberRepository.deleteById(id);
            message = messageSource.getMessage("entity.deleted", new Object[]{"Member"}, Locale.US);
        } catch (Exception e) {
            message = messageSource.getMessage("entity.deleted.error", new Object[]{"Member"}, Locale.US);
            throw new Exception(message);
        }
        
        return message;
        
    }

	@Override
    public List<MemberDto> getAllMemberDTOs() throws Exception {
        return memberRepository.findAll().stream().map(member -> new MemberDto(
            member.getName(), member.getFacebookUrl(),
            member.getInstagramUrl(), member.getLinkedinUrl(),
            member.getImage(), member.getDescription())).collect(Collectors.toList());
	}

	@Override
	public PageDto<MemberDto> getMembersByPage(int page, UriComponentsBuilder uriBuilder) {
		Pageable pageable = PageRequest.of(page,PAGE_SIZE);
		Page<Members> membersPage = memberRepository.findAll(pageable);
		PageDto<MemberDto> pageDto = new PageDto<>();

		if(!(page == 0)){
			pageDto.getLinks().put("prev",buildUrlPage(page-1,uriBuilder));
		}
		if(!(page == membersPage.getTotalPages()-1)){
			pageDto.getLinks().put("next",buildUrlPage(page+1,uriBuilder));
		}
		pageDto.setContent(mapMembersToDto(membersPage.getContent()));
		return pageDto;
	}

	public String buildUrlPage(int page,UriComponentsBuilder uriBuilder){
		return uriBuilder.toUriString() + "/members?page=" + page;
	}

	public List<MemberDto> mapMembersToDto(List<Members> members){
		return members.stream().map(e -> new MemberDto(
				e.getName(),
				e.getFacebookUrl(),
				e.getInstagramUrl(),
				e.getLinkedinUrl(),
				e.getImage(),
				e.getDescription()
		)).collect(Collectors.toList());
	}

}
