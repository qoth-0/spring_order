package com.encore.ordering.order.service;

import com.encore.ordering.item.domain.Item;
import com.encore.ordering.item.repository.ItemRepository;
import com.encore.ordering.member.domain.Member;
import com.encore.ordering.member.repository.MemberRepository;
import com.encore.ordering.order.domain.OrderStatus;
import com.encore.ordering.order.domain.Ordering;
import com.encore.ordering.order.dto.OrderReqDto;
import com.encore.ordering.order.dto.OrderResDto;
import com.encore.ordering.order.repository.OrderRepository;
import com.encore.ordering.order_item.domain.OrderItem;
import com.encore.ordering.order_item.repository.OrderItemRepository;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(OrderRepository orderRepository, MemberRepository memberRepository, ItemRepository itemRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
        this.itemRepository = itemRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public Ordering create(List<OrderReqDto> orderReqDtos) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // getName은 setSubject로 저장된 pk를 꺼냄
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("not found email"));
        Ordering ordering = Ordering.builder().member(member).build();
//        Ordering 객체가 생성될 때 OrderingItem객체도 함께 생성 : cascading
        for(OrderReqDto dto : orderReqDtos) {
            Item item = itemRepository.findById(dto.getItemId()).orElseThrow(() -> new EntityNotFoundException("not found item"));
            OrderItem orderItem = OrderItem.builder()
                    .ordering(ordering)
                    .item(item)
                    .quantity(dto.getCount())
                    .build();
            ordering.getOrderItems().add(orderItem);
            if(item.getStockQuantity() - dto.getCount() < 0) {
                throw new IllegalArgumentException("재고가 부족합니다.");
            }
            orderItem.getItem().updateStockQuantity(item.getStockQuantity() - dto.getCount());
        }
        return orderRepository.save(ordering); // OrderItem:케스케이딩, Item:더티체킹
    }

    public Ordering cancel(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Ordering ordering = orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("not found order"));
//        로그인한 사용자와 주문한 사용자의 email이 다르거나(본인이 아닐 경우) admin이 아닌 경우 취소 불가
        if(!ordering.getMember().getEmail().equals(email) && !authentication.getAuthorities().contains((new SimpleGrantedAuthority("ROLE_ADMIN")))) {
            throw new AccessDeniedException("권한이 없습니다.");
        }
//        상태 변경 ORDERED -> CANCELED
        if(ordering.getOrderStatus() == OrderStatus.CANCELED) {
            throw new IllegalArgumentException("이미 취소된 주문입니다.");
        }
        ordering.cancelOrder();
//        Item수량 복원
        for(OrderItem orderItem : ordering.getOrderItems()) {
            orderItem.getItem().updateStockQuantity(orderItem.getItem().getStockQuantity() + orderItem.getQuantity());
        }
        return ordering;
    }

    public List<OrderResDto> findAll() {
        List<Ordering> orderings = orderRepository.findAll();
        return orderings.stream().map(o -> OrderResDto.toDto(o)).collect(Collectors.toList());
    }

    public List<OrderResDto> findByMember(Long id) {
//        Member member = memberRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
//        List<Ordering> orderings = member.getOrderings();
        List<Ordering> orderings = orderRepository.findByMemberId(id);
        return orderings.stream().map(o->OrderResDto.toDto(o)).collect(Collectors.toList());
    }

    public List<OrderResDto> findMyOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("not found member"));
        List<Ordering> orderings = member.getOrderings();
        return orderings.stream().map(o -> OrderResDto.toDto(o)).collect(Collectors.toList());
    }
}
