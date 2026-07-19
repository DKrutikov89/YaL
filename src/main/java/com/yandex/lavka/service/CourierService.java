package com.yandex.lavka.service;

import com.yandex.lavka.model.dto.CourierDto;
import com.yandex.lavka.model.dto.CreateCourierDto;
import com.yandex.lavka.model.entity.Courier;
import com.yandex.lavka.model.enums.CourierType;
import com.yandex.lavka.repository.CourierRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourierService {

    private static final Logger logger = LoggerFactory.getLogger(CourierService.class);

    private final CourierRepository courierRepository;

    public CourierService(CourierRepository courierRepository) {
        this.courierRepository = courierRepository;
        logger.info("CourierService initialized with repository-backed storage");
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "courierList", allEntries = true),
            @CacheEvict(value = "courierDetails", allEntries = true),
            @CacheEvict(value = "courierMetaInfo", allEntries = true)
    })
    public List<CourierDto> createCouriers(List<CreateCourierDto> createCourierDtos) {
        logger.info("Creating {} couriers", createCourierDtos.size());

        List<Courier> couriers = createCourierDtos.stream()
                .map(this::convertToEntity)
                .toList();

        return courierRepository.saveAll(couriers).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "courierList", key = "'all'")
    public List<CourierDto> getAllCouriers() {
        logger.debug("Fetching all couriers");

        return courierRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "courierList", key = "'page:' + #limit + ':' + #offset")
    public List<CourierDto> getCouriersWithPagination(int limit, int offset) {
        logger.debug("Fetching couriers with limit={} and offset={}", limit, offset);

        return courierRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .skip(offset)
                .limit(limit)
                .map(this::convertToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "courierDetails", key = "#courierId")
    public Optional<CourierDto> getCourierById(Long courierId) {
        logger.debug("Fetching courier by id={}", courierId);

        return courierRepository.findById(courierId)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<CourierDto> findByCourierType(CourierType courierType) {
        logger.debug("Fetching couriers by type={}", courierType);

        return courierRepository.findByCourierTypeOrderByIdAsc(courierType).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CourierDto> findByCourierType(CourierType courierType, int limit, int offset) {
        logger.debug("Fetching couriers by type={} with limit={} and offset={}", courierType, limit, offset);

        return courierRepository.findByCourierTypeOrderByIdAsc(courierType).stream()
                .skip(offset)
                .limit(limit)
                .map(this::convertToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CourierDto> findByRegion(Integer region) {
        logger.debug("Fetching couriers by region={}", region);

        return courierRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .filter(courier -> courier.getRegions() != null && courier.getRegions().contains(region))
                .map(this::convertToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CourierDto> findByRegion(Integer region, int limit, int offset) {
        logger.debug("Fetching couriers by region={} with limit={} and offset={}", region, limit, offset);

        return courierRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .filter(courier -> courier.getRegions() != null && courier.getRegions().contains(region))
                .skip(offset)
                .limit(limit)
                .map(this::convertToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public long countCouriers() {
        return courierRepository.count();
    }

    private Courier convertToEntity(CreateCourierDto dto) {
        Courier courier = new Courier();
        courier.setCourierType(dto.getCourierType());
        courier.setRegions(dto.getRegions());
        courier.setWorkingHours(dto.getWorkingHours());
        return courier;
    }

    private CourierDto convertToDto(Courier entity) {
        return new CourierDto(
                entity.getId(),
                entity.getCourierType(),
                entity.getRegions(),
                entity.getWorkingHours()
        );
    }
}
