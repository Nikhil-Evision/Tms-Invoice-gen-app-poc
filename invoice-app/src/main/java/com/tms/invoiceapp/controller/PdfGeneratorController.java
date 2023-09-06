package com.tms.invoiceapp.controller;

import com.tms.invoiceapp.service.PdfGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/pdf")
public class PdfGeneratorController {
    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    Logger LOGGER = LoggerFactory.getLogger(PdfGeneratorController.class);
    @PostMapping("/create-pdf")
    public ResponseEntity<InputStreamResource> createPdf(
            @RequestParam("companySealImage") MultipartFile companySealImage,
            @RequestParam("authoritySignImage") MultipartFile authoritySignImage,
            @RequestParam("invoiceId") String invoiceId,
            @RequestParam("clientId") Integer clientId,
            @RequestParam  List<Integer> workIds,
            @RequestParam("consigneeId") Integer consigneeId,
            @RequestParam("gstRate") Double gstRate
    )//("workIds")
    {
        ByteArrayInputStream pdf = pdfGeneratorService.createPdf(companySealImage, authoritySignImage,invoiceId,
                clientId,workIds,consigneeId,gstRate);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Disposition", "inline;file=testFile.pdf");

        return ResponseEntity.ok().headers(httpHeaders).contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdf));
    }
}
