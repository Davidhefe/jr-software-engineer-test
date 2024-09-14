package com.adobe.bookstore.repository;

import com.adobe.bookstore.model.BookOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookOrderRepository extends JpaRepository<BookOrderItem, String> {
}
