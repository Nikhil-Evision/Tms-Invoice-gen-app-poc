package com.tms.invoiceapp.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface PdfGeneratorService {
    ByteArrayInputStream createPdf(MultipartFile companySealImage, MultipartFile authoritySignImage,
                                   String invoiceId, int clientId, List<Integer> workIds,
                                   int consigneeId,double gstRate);
}
