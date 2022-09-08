package com.test.repository;

import com.test.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemsRepository  extends JpaRepository<OrderItem,Integer> {
}
