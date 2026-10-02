package com.rentrix.rentrixserver.service;

import com.rentrix.rentrixserver.dto.CreateReviewRequest;
import com.rentrix.rentrixserver.dto.PageResponse;
import com.rentrix.rentrixserver.dto.ReviewDto;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
	
	PageResponse<ReviewDto> getAllReviews(Pageable pageable);
	
	ReviewDto getReviewById(Long id);
	
	PageResponse<ReviewDto> getReviewsByFlat(Long flatId, Pageable pageable);
	
	ReviewDto createReview(Long flatId, Long userId, CreateReviewRequest req);
	
	PageResponse<ReviewDto> getMyReviews(Long userId, Pageable pageable);
	
	String deleteReview(Long id);
	
}