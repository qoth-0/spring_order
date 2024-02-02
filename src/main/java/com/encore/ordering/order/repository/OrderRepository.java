package com.encore.ordering.order.repository;

import com.encore.ordering.order.domain.Ordering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface OrderRepository extends JpaRepository<Ordering, Long> {
}
