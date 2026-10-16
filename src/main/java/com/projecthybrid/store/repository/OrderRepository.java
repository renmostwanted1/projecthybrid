package com.projecthybrid.store.repository;

import com.projecthybrid.store.model.StoreOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<StoreOrder, Long> {}