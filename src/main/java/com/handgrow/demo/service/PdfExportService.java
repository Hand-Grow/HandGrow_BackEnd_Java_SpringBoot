package com.handgrow.demo.service;

/**
 * Service contract for generating contract PDF files.
 * Separated as an interface so the implementation can live under service.impl like other services.
 */
public interface PdfExportService {

    /**
     * Generate a contract PDF as a byte array.
     *
     * @param cooperativeName human-readable coop name
     * @param enterpriseName human-readable enterprise name
     * @param value formatted total value string (e.g. "1000000 VNĐ")
     * @param terms contract terms / AI-generated clauses
     * @param enterpriseSignatoryName name of the enterprise signatory
     * @param enterpriseSignDate date of the enterprise signatory
     * @return PDF bytes
     */
    byte[] generateContractPdf(
            String cooperativeName,
            String enterpriseName,
            String value,
            String terms,
            String enterpriseSignatoryName,
            String enterpriseSignDate);
}
