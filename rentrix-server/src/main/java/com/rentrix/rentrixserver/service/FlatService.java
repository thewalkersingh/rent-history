package com.rentrix.rentrixserver.service;

import com.rentrix.rentrixserver.dto.PageResponse;
import com.rentrix.rentrixserver.dto.filter.FlatFilterRequest;
import com.rentrix.rentrixserver.dto.request.CreateFlatRequest;
import com.rentrix.rentrixserver.dto.request.UpdateFlatRequest;
import com.rentrix.rentrixserver.dto.response.FlatResponse;
import com.rentrix.rentrixserver.dto.response.FlatSummaryResponse;
import org.springframework.data.domain.Pageable;

public interface FlatService {
	
	PageResponse<FlatSummaryResponse> listFlats(FlatFilterRequest filter, Pageable pageable);
	
	FlatResponse getFlatById(Long id);
	
	FlatResponse createFlat(CreateFlatRequest request, Long ownerId);
	
	FlatResponse updateFlat(Long id, UpdateFlatRequest request, Long requesterId, boolean isAdmin);
	
	void deleteFlat(Long id, Long requesterId, boolean isAdmin);
	
}
/*
Design:
- createFlat takes ownerId explicitly — derived from the JWT by the controller
- updateFlat / deleteFlat take requesterId + isAdmin so the service can enforce
ownership without knowing about Spring Security
- List returns FlatSummaryResponse (lighter payload)
 */