package com.franzoso.repositories;

import com.franzoso.domain.event.Event;
import com.franzoso.domain.event.EventAddressProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    @Query("SELECT e.id AS id, e.title AS title, e.description AS description, e.date AS date, e.imgUrl AS imgUrl, e.eventUrl AS eventUrl, e.remote AS remote, a.city AS city, a.state AS state " +
            "FROM Event e LEFT JOIN Address a ON e.id = a.event.id " +
            "WHERE e.date >= :currentDate")
    public Page<EventAddressProjection> findUpcomingEvents(@Param("currentDate") Date currentDate, Pageable pageable);

    @Query("SELECT e.id AS id, e.title AS title, e.description AS description, e.date AS date, e.imgUrl AS imgUrl, e.eventUrl AS eventUrl, e.remote AS remote, a.city AS city, a.state AS state " +
            "FROM Event e JOIN Address a ON e.id = a.event.id " +
            "WHERE (:city = '' OR a.city LIKE %:city%) " +
            "AND (:state = '' OR a.state LIKE %:state%) " +
            "AND (e.date >= :startDate AND e.date <= :endDate)")
    Page<EventAddressProjection> findFilteredEvents(@Param("city") String city,
                                                    @Param("state") String state,
                                                    @Param("startDate") Date startDate,
                                                    @Param("endDate") Date endDate,
                                                    Pageable pageable);

}
