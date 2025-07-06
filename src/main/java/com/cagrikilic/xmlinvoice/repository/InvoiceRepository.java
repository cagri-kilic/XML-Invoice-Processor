package com.cagrikilic.xmlinvoice.repository;

import com.cagrikilic.xmlinvoice.model.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
} 