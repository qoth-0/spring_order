package com.encore.ordering.order_item.repository;

import com.encore.ordering.member.domain.Member;
import com.encore.ordering.order.domain.Ordering;
import com.encore.ordering.order_item.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Optional<OrderItem> findByOrdering(Ordering ordering);
}
