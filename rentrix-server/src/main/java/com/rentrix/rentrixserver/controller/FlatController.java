package com.rentrix.rentrixserver.controller;

import com.rentrix.rentrixserver.dto.PageResponse;
import com.rentrix.rentrixserver.dto.filter.FlatFilterRequest;
import com.rentrix.rentrixserver.dto.request.CreateFlatRequest;
import com.rentrix.rentrixserver.dto.request.UpdateFlatRequest;
import com.rentrix.rentrixserver.dto.response.FlatResponse;
import com.rentrix.rentrixserver.dto.response.FlatSummaryResponse;
import com.rentrix.rentrixserver.security.CustomUserDetails;
import com.rentrix.rentrixserver.service.FlatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/flats")
@RequiredArgsConstructor
public class FlatController {
	
	private final FlatService flatService;
	
	/** Public: list flats with optional filters. */
	@GetMapping
	public PageResponse<FlatSummaryResponse> listFlats(@ModelAttribute FlatFilterRequest filter,
		@PageableDefault(size = 9, sort = "rent", direction = Sort.Direction.ASC) Pageable pageable) {
		return flatService.listFlats(filter, pageable);
	}
	
	/** Public: flat detail. */
	@GetMapping("/{id}")
	public ResponseEntity<FlatResponse> getFlatById(@PathVariable Long id) {
		
		return ResponseEntity.ok(flatService.getFlatById(id));
	}
	
	/** LANDLORD or ADMIN: create a flat. */
	@PostMapping
	@PreAuthorize("hasAnyRole('LANDLORD','ADMIN')")
	public ResponseEntity<FlatResponse> createFlat(@Valid @RequestBody CreateFlatRequest request,
		@AuthenticationPrincipal CustomUserDetails principal) {
		
		FlatResponse created = flatService.createFlat(request, principal.getId());
		
		return ResponseEntity.status(201).body(created);
	}
	
	/** Owner or ADMIN: partial update. */
	@PatchMapping("/{id}")
	@PreAuthorize("hasAnyRole('LANDLORD','ADMIN')")
	public ResponseEntity<FlatResponse> updateFlat(@PathVariable Long id,
		@Valid @RequestBody UpdateFlatRequest request, @AuthenticationPrincipal CustomUserDetails principal) {
		
		boolean isAdmin = "ADMIN".equals(principal.getRole());
		FlatResponse updated = flatService.updateFlat(id, request, principal.getId(), isAdmin);
		
		return ResponseEntity.ok(updated);
	}
	
	/** Owner or ADMIN: soft-delete. */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('LANDLORD','ADMIN')")
	public ResponseEntity<Void> deleteFlat(@PathVariable Long id,
		@AuthenticationPrincipal CustomUserDetails principal) {
		
		boolean isAdmin = "ADMIN".equals(principal.getRole());
		flatService.deleteFlat(id, principal.getId(), isAdmin);
		
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/debug/h2-console")
	public Map<String, Object> debugH2(
		@Value("${spring.h2.console.enabled:NOT_SET}") String enabled,
		@Value("${spring.h2.console.path:NOT_SET}") String path) {
		return Map.of("enabled", enabled, "path", path);
	}
	
}