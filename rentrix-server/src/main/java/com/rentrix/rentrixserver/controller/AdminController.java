package com.rentrix.rentrixserver.controller;

import com.rentrix.rentrixserver.dto.ModerateReviewRequest;
import com.rentrix.rentrixserver.dto.PageResponse;
import com.rentrix.rentrixserver.dto.ReviewDto;
import com.rentrix.rentrixserver.entity.constants.ReviewStatus;
import com.rentrix.rentrixserver.service.AdminService;
import com.rentrix.rentrixserver.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
	
	private final AdminService adminService;
	private final ReviewService reviewService;
	
	// -- Moderation queue ----------------------------------------------------
	
	/** List reviews filtered by status (default PENDING). */
	@GetMapping("/reviews")
	public PageResponse<ReviewDto> listReviews(@RequestParam(defaultValue = "PENDING") ReviewStatus status,
		@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		return adminService.getReviewsByStatus(status, pageable);
	}
	
	/** Approve or reject a pending review. */
	@PatchMapping("/reviews/{id}")
	public ResponseEntity<ReviewDto> moderateReview(@PathVariable Long id,
		@Valid @RequestBody ModerateReviewRequest req) {
		return ResponseEntity.ok(adminService.moderate(id, req.getStatus()));
	}
	
	// -- Direct review CRUD (admin override) --------------------------------
	
	/** Get any review by ID regardless of status. */
	@GetMapping("/reviews/{id}")
	public ResponseEntity<ReviewDto> getReviewById(@PathVariable Long id) {
		return ResponseEntity.ok(reviewService.getReviewById(id));
	}
	
	/** Delete a review (moderation cleanup). */
	@DeleteMapping("/reviews/{id}")
	public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
		reviewService.deleteReview(id);
		return ResponseEntity.noContent().build();
	}
	
	//TODO: Change Review assignment to given flat (Optional)
}
/*
Note:We are using @PreAuthorize("hasRole('ADMIN')") on the class — every method requires ADMIN. Combined with
.requestMatchers("/admin/**").hasRole("ADMIN") in SecurityConfig, this is double-Check.
 */