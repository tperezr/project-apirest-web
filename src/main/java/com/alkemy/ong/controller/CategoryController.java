package com.alkemy.ong.controller;

import java.util.List;
import java.util.Locale;
import javax.validation.constraints.Min;
import com.alkemy.ong.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.alkemy.ong.exception.EntityNotFoundException;
import com.alkemy.ong.exception.ONGBadRequestException;
import com.alkemy.ong.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "Categories")
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {
    private final CategoryService categoryService;
    private final MessageSource messageSource;

    @Operation(summary = "Get details Category of and id", description = "Returns a Category in detail by id for an Admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Category not found with the id"),
            @ApiResponse(responseCode = "403", description = "Don't have admin permissions"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryDetailsById(
            @PathVariable @Min(value = 1,message = "Id must be greater than or equal to 1") Long id){
        CategoryResponse category = categoryService.findCategoryById(id);
        if(category == null){
            String mssg = messageSource.getMessage("error.entity.not-found-id",new Object[]{"Category",id}, Locale.US);
            throw new EntityNotFoundException(mssg);
        }
        return ResponseEntity.ok(category);
    }

    @Operation(summary="Updates a Category by the given id", description = "Returns if the Category was updated or not.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category Successfully updated."),
            @ApiResponse(responseCode = "400", description = "Missing or invalid request body"),
            @ApiResponse(responseCode = "403", description = "Don't have admin permissions"),
            @ApiResponse(responseCode = "404", description = "Error. The Category was not found."),
            @ApiResponse(responseCode = "409", description = "Error. The Category could not be updated."),
            @ApiResponse(responseCode = "415", description = "Media type unsupported.")
    })

    @PutMapping(value ="/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @ModelAttribute CategoryRequest categoryRequest){

    	if(categoryRequest == null)
    		throw new ONGBadRequestException(messageSource.getMessage("error.controller.missing-dto", new Object[] {"Category data or Image"}, Locale.US));
    	return ResponseEntity.status(HttpStatus.OK).body(categoryService.updateCategory(id, categoryRequest));
    }


    @Operation(summary="Deletes a Category by the give id", description = "Returns if the Category could be deleted or not.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category Successfully deleted."),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Don't have admin permissions"),
            @ApiResponse(responseCode = "404", description = "Error. The Category was not found.")
    })
    @DeleteMapping("/{id}") 
	public ResponseEntity<String>delete(@PathVariable(value="id", required=true) Long id){
		try {
			return ResponseEntity.status(HttpStatus.OK).body(categoryService.delete(id));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

    @Operation(summary="Creates a new Category", description = "Returns if the Category was created or not.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category Successfully created."),
            @ApiResponse(responseCode = "400", description = "Missing or invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Don't have admin permissions"),
            @ApiResponse(responseCode = "409", description = "Error The Category coul not be added."),
            @ApiResponse(responseCode = "415", description = "Media type unsupported.")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE) 
	public ResponseEntity<String> create(@ModelAttribute CategoryRequest categoryRequest){
    	if(!categoryRequest.hasName() || !categoryRequest.getName().matches("[a-zA-Z]+")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageSource.getMessage("error.controller.missing-dto", new Object[]{"Category"}, Locale.US));    	
        }
		return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.save(categoryRequest));
	}

    @Operation(summary = "Get a list of all Category", description = "Returns a list of Category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
    })
    @GetMapping()
    public ResponseEntity<?> getAllCategoriesName(){
        List <CategoryResponse> categories = categoryService.findAllname();
        if(categories.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(messageSource.getMessage("no.records.found", new Object[]{"Category"}, Locale.US));
        }
            return ResponseEntity.status(HttpStatus.OK).body(categories);
    }

    @Operation(summary="Get Category", description = "Returns testimonials for page.")
    @GetMapping(params = "page")
    public ResponseEntity<?> getPagedCategories(@RequestParam(name = "page", required = false) int page, UriComponentsBuilder uriComponentsBuilder){
        PageDto<CategoryResponse> pageDto = categoryService.getCategoriesByPage(page, uriComponentsBuilder);
        if(pageDto.getContent().isEmpty()){
            String msg = messageSource.getMessage("error.entity.empty",new Object[]{"Categories"},Locale.US);
            return ResponseEntity.ok(msg);
        }
        return ResponseEntity.ok(pageDto);
    }
    
}
