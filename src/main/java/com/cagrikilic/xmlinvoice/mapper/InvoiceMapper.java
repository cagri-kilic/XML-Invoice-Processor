package com.cagrikilic.xmlinvoice.mapper;

import com.cagrikilic.xmlinvoice.generated.local.Faktura;
import com.cagrikilic.xmlinvoice.model.entity.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nip", expression = "java(faktura.getPodmiot1().getDaneIdentyfikacyjne().getNIP())")
    @Mapping(target = "p1", expression = "java(faktura.getFa().getP1().toString())")
    @Mapping(target = "p2", expression = "java(faktura.getFa().getP2().toString())")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    Invoice fakturaToInvoice(Faktura faktura);
} 