package com.example.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {

    Optional<Inventory> findByProductName(String productName);

    @Modifying
    @Query("UPDATE Inventory i SET i.stock = i.stock - :quantity WHERE i.productName = :productName AND i.stock > 0")
    int updateStock(@Param("productName") String productName, Integer quantity);
}
