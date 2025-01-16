package com.franzoso.service;


import com.franzoso.domain.address.Address;
import com.franzoso.domain.coupon.Coupon;
import com.franzoso.domain.event.*;
import com.franzoso.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {

    @Value("${aws.bucket.name}")
    private String bucketName;

    @Value("${admin.key}")
    private String adminKey;

    private final S3Client s3Client;
    private final AddressService addressService;
    private final CouponService couponService;
    private final EventRepository eventRepository;

    public Event createEvent(EventRequestDTO requestDTO) {
        String imgUrl = "";
        if(requestDTO.image() != null) {
            imgUrl = this.uploadImg(requestDTO.image());
        }
        Event newEvent = new Event();
        newEvent.setTitle(requestDTO.title());
        newEvent.setDescription(requestDTO.description());
        newEvent.setRemote(requestDTO.remote());
        newEvent.setEventUrl(requestDTO.eventUrl());
        newEvent.setDate(new Date(requestDTO.date()));
        newEvent.setImgUrl(imgUrl);
        eventRepository.save(newEvent);
        if(!requestDTO.remote()) {
            addressService.createAddress(requestDTO, newEvent);
        }
        return newEvent;
    }

    public List<EventResponseDTO> getUpcomingEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EventAddressProjection> eventsPage = eventRepository.findUpcomingEvents(new Date(), pageable);
        return eventsPage.map(event -> new EventResponseDTO(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getDate(),
                        event.getCity() != null ? event.getCity() : "",
                        event.getState() != null ? event.getState() : "",
                        event.getRemote(),
                        event.getEventUrl(),
                        event.getImgUrl())
                )
                .stream().toList();
    }

    public List<EventResponseDTO> getFilteredEvents(int page, int size, String city, String state, Date startDate, Date endDate){
        city = (city != null) ? city : "";
        state = (state != null) ? state : "";
        startDate = (startDate != null) ? startDate : new Date(0);
        endDate = (endDate != null) ? endDate : new Date();

        Pageable pageable = PageRequest.of(page, size);
        Page<EventAddressProjection> eventsPage = this.eventRepository.findFilteredEvents(city, state, startDate, endDate, pageable);
        return eventsPage.map(event -> new EventResponseDTO(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getDate(),
                        event.getCity() != null ? event.getCity() : "",
                        event.getState() != null ? event.getState() : "",
                        event.getRemote(),
                        event.getEventUrl(),
                        event.getImgUrl())
                )
                .stream().toList();
    }

    public EventDetailsDTO getEventDetails(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        Optional<Address> address = addressService.findByEventId(eventId);

        List<Coupon> coupons = couponService.consultCoupons(eventId, new Date());

        List<EventDetailsDTO.CouponDTO> couponDTOs = coupons.stream()
                .map(coupon -> new EventDetailsDTO.CouponDTO(
                        coupon.getCode(),
                        coupon.getDiscount(),
                        coupon.getValid()))
                .collect(Collectors.toList());

        return new EventDetailsDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                address.isPresent() ? address.get().getCity() : "",
                address.isPresent() ? address.get().getState() : "",
                event.getImgUrl(),
                event.getEventUrl(),
                couponDTOs);
    }

    private String uploadImg(MultipartFile multipartFile) {
        String filename = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();
        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .build();
            s3Client.putObject(putOb, RequestBody.fromByteBuffer(ByteBuffer.wrap(multipartFile.getBytes())));
            GetUrlRequest request = GetUrlRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .build();
            return s3Client.utilities().getUrl(request).toString();
        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage());
            return "";
        }
    }
}
