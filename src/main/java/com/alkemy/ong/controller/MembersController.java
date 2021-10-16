package com.alkemy.ong.controller;

import java.util.List;
import java.util.Locale;

import com.alkemy.ong.dto.MemberDto;
import com.alkemy.ong.dto.MembersRequest;
import com.alkemy.ong.dto.PageDto;
import com.alkemy.ong.exception.ONGBadRequestException;
import com.alkemy.ong.service.MembersService;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Members")
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Validated
public class MembersController {
    
    private final MessageSource messageSource;
    private final MembersService membersService;

    @Operation(summary = "Get list members", description = "Return all members")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Members found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping()
    public ResponseEntity<?> getAllMemberDTOs() throws Exception{
        List <MemberDto> memberDTOs = membersService.getAllMemberDTOs();
        if(memberDTOs.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(messageSource.getMessage("no.records.found", new Object[]{"Members"}, Locale.US));
        }
        return ResponseEntity.status(HttpStatus.OK).body(memberDTOs);
    }

    @Operation(summary = "Create a new member", description = "Return if a new member was created or not.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Member created"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> createMember(@ModelAttribute MembersRequest memberRequest){

    	if(memberRequest == null)
    		throw new ONGBadRequestException(messageSource.getMessage("error.controller.missing-dto", new Object[] {"Member data or Image"}, Locale.US));
    	
		return ResponseEntity.status(HttpStatus.CREATED).body(membersService.createMember(memberRequest));
	}

    @Operation(summary = "Update a created member by the id", description = "Return if a new member was updated or not.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Member created"),
        @ApiResponse(responseCode = "400", description = "Missing or invalid request body")
    })
	@PutMapping(value = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> updateMember(
			@PathVariable long id, @ModelAttribute MembersRequest memberRequest){

    	if(memberRequest == null)
    		throw new ONGBadRequestException(messageSource.getMessage("error.controller.missing-dto", new Object[] {"Member data or Image"}, Locale.US));
    	
		return ResponseEntity.status(HttpStatus.OK).body(membersService.updateMember(id, memberRequest));
	}
	
    @Operation(summary="Deletes a member by the give id", description = "Returns if the member is deleted or not.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Member Successfully created."), 
			@ApiResponse(responseCode = "404", description = "Error. The Member was not found."), 
			@ApiResponse(responseCode = "400", description = "Missing or invalid request body")
    })
	@DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable(value = "id", required = true) Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(membersService.delete(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Get list members", description = "Return a page of 10 members")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Members found")
    })
    @GetMapping(params = "page")
    public ResponseEntity<?> getMembersByPage(@RequestParam int page, UriComponentsBuilder uriComponentsBuilder){
        PageDto<MemberDto> pageDto = membersService.getMembersByPage(page, uriComponentsBuilder);
        if(pageDto.getContent().isEmpty()){
            String mssg = messageSource.getMessage("error.entity.empty",new Object[]{"Members"},Locale.US);
            return ResponseEntity.ok(mssg);
        }
        return ResponseEntity.ok(pageDto);
    }
}
