package com.rentrix.rentrixserver.service.impl;

import com.rentrix.rentrixserver.dto.PageResponse;
import com.rentrix.rentrixserver.dto.filter.FlatFilterRequest;
import com.rentrix.rentrixserver.dto.request.CreateFlatRequest;
import com.rentrix.rentrixserver.dto.request.UpdateFlatRequest;
import com.rentrix.rentrixserver.dto.response.FlatResponse;
import com.rentrix.rentrixserver.dto.response.FlatSummaryResponse;
import com.rentrix.rentrixserver.entity.Flat;
import com.rentrix.rentrixserver.entity.User;
import com.rentrix.rentrixserver.exception.ApiException;
import com.rentrix.rentrixserver.mapper.FlatMapper;
import com.rentrix.rentrixserver.repository.FlatRepository;
import com.rentrix.rentrixserver.repository.UserRepository;
import com.rentrix.rentrixserver.repository.specification.FlatSpecification;
import com.rentrix.rentrixserver.service.FlatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlatServiceImpl implements FlatService {
	
	private final FlatRepository flatRepository;
	private final UserRepository userRepository;
	
	@Override
	public PageResponse<FlatSummaryResponse> listFlats(FlatFilterRequest filter, Pageable pageable) {
		log.info("List flats with filters: {}", filter);
		return PageResponse.from(
			flatRepository.findAll(FlatSpecification.withFilters(filter), pageable),
			FlatMapper::toSummary
		);
	}
	
	@Override
	public FlatResponse getFlatById(Long id) {
		Flat flat = flatRepository
							.findById(id)
							.orElseThrow(() -> ApiException.notFound("Flat not found with id: " + id));
		return FlatMapper.toResponse(flat);
	}
	
	@Override
	@Transactional
	public FlatResponse createFlat(CreateFlatRequest request, Long ownerId) {
		User owner = userRepository.findById(ownerId)
											.orElseThrow(() -> ApiException.notFound("Owner not found"));
		Flat flat = FlatMapper.toEntity(request);
		flat.setOwner(owner);
		
		Flat savedFlat = flatRepository.save(flat);
		log.info("Flat created: id={}, owner={}", savedFlat.getId(), ownerId);
		
		return FlatMapper.toResponse(savedFlat);
	}
	
	@Override
	@Transactional
	public FlatResponse updateFlat(Long id, UpdateFlatRequest request, Long requesterId, boolean isAdmin) {
		Flat flat = flatRepository
							.findById(id)
							.orElseThrow(() -> ApiException.notFound("Flat not found with id: " + id));
		
		// Ownership check
		if (!isAdmin && (flat.getOwner() == null || !flat.getOwner().getId().equals(requesterId))) {
			throw ApiException.forbidden("You can only update your own flats");
		}
		
		FlatMapper.copyTo(flat, request);
		Flat savedFlat = flatRepository.save(flat);
		log.info("Flat updated: id={} by user={}", id, requesterId);
		return FlatMapper.toResponse(savedFlat);
	}
	
	@Override
	@Transactional
	public void deleteFlat(Long id, Long requesterId, boolean isAdmin) {
		Flat flat = flatRepository.findById(id)
										  .orElseThrow(() -> ApiException.notFound("Flat not found with id: " + id));
		
		if (!isAdmin && (flat.getOwner() == null || !flat.getOwner().getId().equals(requesterId))) {
			throw ApiException.forbidden("You can only delete your own flats");
		}
		flatRepository.deleteById(id); // this will trigger soft delete -> @SQLDelete
		log.info("Flat soft-deleted: id={} by user={}", id, requesterId);
	}
	
}
/*
NOTES:
- @SQLRestriction hides deleted rows from all queries — including findById;
- @SQLDelete on the entity makes delete() perform UPDATE flats SET deleted = true
 */