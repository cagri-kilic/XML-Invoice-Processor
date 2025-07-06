package com.cagrikilic.xmlinvoice.config;

import com.cagrikilic.xmlinvoice.generated.local.Faktura;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

@Configuration
@Slf4j
public class XmlValidationConfig {

    @Bean
    public Schema xmlSchema() throws SAXException {
        log.info("Initializing XML Schema...");

        System.setProperty("jdk.xml.maxOccurLimit", "10000");
        System.setProperty("jdk.xml.totalEntitySizeLimit", "10000000");

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(getClass().getResource("/xsd/schemat.xsd"));

        log.info("XML Schema initialized successfully");
        return schema;
    }

    @Bean
    public JAXBContext jaxbContext() throws JAXBException {
        log.info("Initializing JAXB Context...");

        JAXBContext context = JAXBContext.newInstance(Faktura.class);

        log.info("JAXB Context initialized successfully");
        return context;
    }
} 