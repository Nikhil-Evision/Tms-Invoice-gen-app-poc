    package com.tms.invoiceapp.service;

    import com.lowagie.text.*;
    import com.lowagie.text.pdf.PdfPCell;
    import com.lowagie.text.pdf.PdfPTable;
    import com.lowagie.text.pdf.PdfWriter;
    import com.tms.invoiceapp.config.BankDetailsProperties;
    import com.tms.invoiceapp.model.*;
    import com.tms.invoiceapp.utility.IndianCurrencyWordConverter;
    import com.tms.invoiceapp.utility.BillingCalculationMethods;
    import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import org.springframework.web.multipart.MultipartFile;
    import java.io.ByteArrayInputStream;
    import java.io.ByteArrayOutputStream;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    @Service
    public class PdfGeneratorServiceImpl implements PdfGeneratorService {
        private static final Logger LOGGER = LoggerFactory.getLogger(PdfGeneratorService.class);
        @Autowired
        private ClientService clientService;
        @Autowired
        private ConsigneeService consigneeService;
        @Autowired
        private InvoiceService invoiceService;
        @Autowired
        private ServicesService servicesService;
        @Autowired
        private WorkInfoService workInfoService;

        //Getting data from the yaml file
        @Autowired
        private BankDetailsProperties bankDetailsProperties;
        @Override
        public ByteArrayInputStream createPdf(MultipartFile companySealImage, MultipartFile authoritySignImage,
                                              String invoiceId,int clientId, List<Integer> workIds,
                                              int consigneeId,double gstRate)
        {
            LOGGER.info("create pdf method Started ---> ");

            //Invoice Info
            Invoice invoice = invoiceService.getInvoiceInfo(invoiceId);
            String invoiceNumber = invoice.getInvoiceNumber();
            String invoiceDate =  invoice.getInvoiceDate().toString();
            String transportMode = invoice.getTransportMode();
            String vehicleNumber = invoice.getVehicleNumber();
            String reverseCharge = String.valueOf(invoice.getReverseCharge());
            String billingPeriod = String.valueOf(invoice.getBillingPeriod());
            String state = invoice.getState();
            String stateCode = invoice.getStateCode();
            String placeOfSupply = invoice.getPlaceOfSupply();

            //Client Info
            Client client = clientService.getClientInfo(clientId);
            String clientName =  client.getClientName();
            String clientAddress = client.getClientAddress();
            String clientGstin = client.getClientGstin();
            String clientCountry = client.getClientCountry();

            //Consignee Info
            Consignee consignee = consigneeService.getConsigneeInfo(consigneeId);
            String consigneeAddress = consignee.getConsigneeAddress();
            String consigneeGstin =   consignee.getConsigneeGstin();
            String consigneeCountry =  consignee.getConsigneeCountry();

            // Using a Map to associate workIds names with the list of integers
            Map<String, WorkInfo> workInfoMap = new HashMap<>(); //Containing all workIds in this map
            for (int i = 0; i < workIds.size(); i++) {
                String variableName = "workInfo" + (i + 1);
                WorkInfo workInfo = workInfoService.getWorkInfo(workIds.get(i));
                workInfoMap.put(variableName, workInfo);
            }
           //Collecting all the services
            Map<String,Services> servicesMap = new HashMap<>();
            int count =1;
            for(WorkInfo workInfo : workInfoMap.values()){
                String key = "service"+count;
                servicesMap.put(key,servicesService.getServicesInfo(workInfo.getServiceId()));
                count++;
            }


            //Invoice Data Section:
            double gstOnReverseCharge=0;
            
            //Banking Details

            String accountNumber =  bankDetailsProperties.getBankAccountNumber();
            String ifsc = bankDetailsProperties.getBankIFSC();
            String bankName = bankDetailsProperties.getBankName();
            String beneficiaryName = bankDetailsProperties.getBeneficiaryName();

                try {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    Document document = new Document(PageSize.A4);

                    PdfWriter.getInstance(document, out);

                    document.open();

                    //Font Style Creation
                    Font h1LabelFont = FontFactory.getFont(FontFactory.TIMES_BOLD,20);
                    Font h2LabelFont = FontFactory.getFont(FontFactory.TIMES_BOLD,12);
                    Font h6LabelFont = FontFactory.getFont(FontFactory.TIMES,6);
                    Font boldFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 10);
                    Font boldSmallFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 8);
                    Font normalFont = FontFactory.getFont(FontFactory.TIMES,10);
                    Font normalSmallFont = FontFactory.getFont(FontFactory.TIMES,8);

                    //For the Header Section --------------------------------------------

                    PdfPTable tableHeading = new PdfPTable(1);
                    tableHeading.setWidthPercentage(100);

                    Paragraph cellHeadingPara = new Paragraph("Evision Software Solution Private Limited \n",h1LabelFont);
                    cellHeadingPara.add(new Chunk(" "));
                    cellHeadingPara.add(new Chunk("87, Dwarkadhish Colony Airport Road \n",h2LabelFont));
                    cellHeadingPara.add(new Chunk("Indore (MP)-452005\n",h2LabelFont));
                    cellHeadingPara.add(new Chunk("Mob. No. 9819890057, Tel. No. 07312610148\n",h2LabelFont));
                    cellHeadingPara.add(new Chunk("GSTIN: 23AAHCE1331J1Z0\n",h2LabelFont));
                    cellHeadingPara.add(new Chunk(" ",h6LabelFont));

                    PdfPCell cellTitle = new PdfPCell(cellHeadingPara);
                    cellTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tableHeading.addCell(cellTitle);

                    //Adding a Blank Cell
                    PdfPCell blankCell = new PdfPCell(new Paragraph(" ",h2LabelFont));
                    tableHeading.addCell(blankCell);

                    //Adding the Invoice Paragraph
                    Paragraph invoiceHeadingPara = new Paragraph("Invoice\n",h1LabelFont);
                    invoiceHeadingPara.add(new Chunk(" ",h2LabelFont));
                    PdfPCell invoiceHeadingCell = new PdfPCell(invoiceHeadingPara);
                    invoiceHeadingCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tableHeading.addCell(invoiceHeadingCell);

                    //Adding a Blank Row

                    PdfPCell blankCellLarge = new PdfPCell(new Paragraph(" ",h2LabelFont));
                    tableHeading.addCell(blankCellLarge);

                    document.add(tableHeading);

                    //Invoice Information section------------------------------------------------------------------

                    PdfPTable invoiceInformationTable = new PdfPTable(2);
                    invoiceInformationTable.setWidthPercentage(100);

                    //Invoice Date
                    Paragraph invoiceInfoDatePara = new Paragraph("Invoice Date: ",boldFont);
                    invoiceInfoDatePara.add(new Chunk(invoiceDate,normalFont));
                    PdfPCell invoiceInfoCell = new PdfPCell(invoiceInfoDatePara);
                    invoiceInfoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    invoiceInformationTable.addCell(invoiceInfoCell);

                    //Transport Mode
                    Paragraph transportModePara = new Paragraph("Transport Mode: ",boldFont);
                    transportModePara.add(new Chunk(transportMode,normalFont));
                    PdfPCell transportModeCell = new PdfPCell(transportModePara);
                    transportModeCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    invoiceInformationTable.addCell(transportModeCell);

                    //Invoice Number
                    Paragraph invoiceInfoNumPara = new Paragraph("Invoice Number: ",boldFont);
                    invoiceInfoNumPara.add(new Chunk(invoiceNumber,normalFont));
                    PdfPCell invoiceInfoNumCell = new PdfPCell(invoiceInfoNumPara);
                    invoiceInfoNumCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    invoiceInformationTable.addCell(invoiceInfoNumCell);

                    //Vehicle Number
                    Paragraph vehicleNumberPara = new Paragraph("Vehicle number: ",boldFont);
                    vehicleNumberPara.add(new Chunk(vehicleNumber,normalFont));
                    PdfPCell vehicleNumberCell = new PdfPCell(vehicleNumberPara);
                    vehicleNumberCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    invoiceInformationTable.addCell(vehicleNumberCell);

                    //Reverse Charge
                    Paragraph ReverseChargePara = new Paragraph("Reverser Charge: ",boldFont);
                    ReverseChargePara.add(new Chunk(reverseCharge,normalFont));
                    PdfPCell reverserChargeCell = new PdfPCell(ReverseChargePara);
                    reverserChargeCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    invoiceInformationTable.addCell(reverserChargeCell);

                    //Billing Period
                    Paragraph billingPeriodPara = new Paragraph("Billing Period: ",boldFont);
                    billingPeriodPara.add(new Chunk(billingPeriod,normalFont));
                    PdfPCell billingPeriodCell = new PdfPCell(billingPeriodPara);
                    billingPeriodCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    invoiceInformationTable.addCell(billingPeriodCell);

                    //State also added the code
                    Paragraph statePara = new Paragraph("State: ",boldFont);
                    statePara.add(new Chunk(state+"                                       ",normalFont));
                    statePara.add(new Chunk("      Code:    ",boldFont));
                    statePara.add(new Chunk(stateCode,normalFont));
                    PdfPCell stateCell = new PdfPCell(statePara);
                    stateCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    invoiceInformationTable.addCell(stateCell);

                    //Place of supply
                    Paragraph placeOfSupplyPara = new Paragraph("Place of Supply: ",boldFont);
                    placeOfSupplyPara.add(new Chunk(placeOfSupply,normalFont));
                    PdfPCell placeOfSupplyCell = new PdfPCell(placeOfSupplyPara);
                    placeOfSupplyCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    invoiceInformationTable.addCell(placeOfSupplyCell);

                    //Adding table to the document
                    document.add(invoiceInformationTable);

                    //Blank Line table
                    PdfPTable blankRow = new PdfPTable(1);
                    blankRow.setWidthPercentage(100);
                    PdfPCell blankSmallCell = new PdfPCell(new Paragraph(" ",normalFont));
                    blankSmallCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    blankRow.addCell(blankSmallCell);
                    document.add(blankRow);

                    //Client Details section-----------------------------------------------------------------------

                    PdfPTable clientDetailsTable = new PdfPTable(2);
                    clientDetailsTable.setWidthPercentage(100);

                    //Billed to header
                    Paragraph billedToLabelPara = new Paragraph("Billed to (Details of Receiver) ",boldFont);
                    PdfPCell billedToLabelCell = new PdfPCell(billedToLabelPara);
                    billedToLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    clientDetailsTable.addCell(billedToLabelCell);

                    //Shipped to header
                    Paragraph shippedToLabelPara = new Paragraph("Shipped to (Details of Consignee) ",boldFont);
                    PdfPCell shippedToLabelCell = new PdfPCell(shippedToLabelPara);
                    shippedToLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    clientDetailsTable.addCell(shippedToLabelCell);

                    //Name cell for Both(billed to and shipped to)
                    Paragraph nameLabelPara = new Paragraph("Name: ",boldFont);
                    nameLabelPara.add(new Chunk(clientName,normalFont));
                    PdfPCell nameLabelCell = new PdfPCell(nameLabelPara);
                    nameLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    clientDetailsTable.addCell(nameLabelCell);

                    PdfPCell blankLabelCell = new PdfPCell(new Paragraph(" ",boldFont));
                    blankLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    clientDetailsTable.addCell(blankLabelCell);

                    //Address Label For billed to Client
                    Paragraph billedAddressLabelPara = new Paragraph("Address: ",boldFont);
                    billedAddressLabelPara.add(new Chunk(clientAddress+"\n\n",normalFont));
                    billedAddressLabelPara.add(new Chunk("GSTIN: ",boldFont));
                    billedAddressLabelPara.add(new Chunk(clientGstin,normalFont));
                    PdfPCell billedAddressLabelCell = new PdfPCell(billedAddressLabelPara);
                    billedAddressLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    clientDetailsTable.addCell(billedAddressLabelCell);

                    //Address Label For shipped to Client
                    Paragraph shippedAddressLabelPara = new Paragraph("Address: ",boldFont);
                    shippedAddressLabelPara.add(new Chunk(consigneeAddress+"\n\n",normalFont));
                    shippedAddressLabelPara.add(new Chunk("GSTIN: ",boldFont));
                    shippedAddressLabelPara.add(new Chunk(consigneeGstin,normalFont));
                    PdfPCell shippedAddressLabelCell = new PdfPCell(shippedAddressLabelPara);
                    shippedAddressLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    clientDetailsTable.addCell(shippedAddressLabelCell);

                    //Country Label for BilledTo
                    Paragraph billedToCountryLabelPara = new Paragraph("Country: ",boldFont);
                    billedToCountryLabelPara.add(new Chunk(clientCountry,normalFont));
                    PdfPCell billedToCountryLabelCell = new PdfPCell(billedToCountryLabelPara);
                    billedToCountryLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    clientDetailsTable.addCell(billedToCountryLabelCell);

                    //Country Label for ShippedTo
                    Paragraph shippedToCountryLabelPara = new Paragraph("Country: ",boldFont);
                    shippedToCountryLabelPara.add(new Chunk(consigneeCountry,normalFont));
                    PdfPCell shippedToCountryLabelCell = new PdfPCell(shippedToCountryLabelPara);
                    shippedToCountryLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    clientDetailsTable.addCell(shippedToCountryLabelCell);

                    //Add table to document
                    document.add(clientDetailsTable);

                    //Blank Line table
                    PdfPTable blankRow2 = new PdfPTable(1);
                    blankRow2.setWidthPercentage(100);
                    PdfPCell blankSmallCell2 = new PdfPCell(new Paragraph(" ",normalFont));
                    blankSmallCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
                    blankRow2.addCell(blankSmallCell2);
                    document.add(blankRow2);

//-------------------------------Invoice Description section-----------------------------------------------------------------

                    PdfPTable invoiceDescTable = new PdfPTable(10);
                    float[] invoiceDescTableHeaderColumnWidths = {5f, 29f, 6f, 13f,8f,13f,5f,8f,5f,8f};
                    invoiceDescTable.setWidths(invoiceDescTableHeaderColumnWidths);
                    invoiceDescTable.setWidthPercentage(100);

                    //Main Header Section
                    PdfPCell snoCell = new PdfPCell(new Paragraph("S.no", boldSmallFont));
                    snoCell.setRowspan(2);
                    snoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(snoCell);

                    PdfPCell productDescCell = new PdfPCell(new Paragraph("Service Description", boldSmallFont));
                    productDescCell.setRowspan(2);
                    productDescCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(productDescCell);

                    PdfPCell hsnCell = new PdfPCell(new Paragraph("HSN Code", boldSmallFont));
                    hsnCell.setRowspan(2);
                    hsnCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(hsnCell);

                    PdfPCell rateInRupeeCell = new PdfPCell(new Paragraph("Rate in Rupee", boldSmallFont));
                    rateInRupeeCell.setRowspan(2);
                    rateInRupeeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(rateInRupeeCell);

                    PdfPCell discountCell = new PdfPCell(new Paragraph("Discount ", boldSmallFont));
                    discountCell.setRowspan(2);
                    discountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(discountCell);

                    PdfPCell taxableValueCell = new PdfPCell(new Paragraph("Taxable Value", boldSmallFont));
                    taxableValueCell.setRowspan(2);
                    taxableValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(taxableValueCell);

                    PdfPCell sgstCell = new PdfPCell(new Paragraph("SGST", boldSmallFont));
                    sgstCell.setColspan(2);
                    sgstCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(sgstCell);

                    PdfPCell cgstCell = new PdfPCell(new Paragraph("CGST", boldSmallFont));
                    cgstCell.setColspan(2);
                    cgstCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(cgstCell);

                    //Second Header

                    PdfPCell sgstRateCell = new PdfPCell(new Paragraph("Rate", boldSmallFont));
                    sgstRateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(sgstRateCell);

                    PdfPCell sgstAmountCell = new PdfPCell(new Paragraph("Amount", boldSmallFont));
                    sgstAmountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(sgstAmountCell);

                    PdfPCell cgstRateCell = new PdfPCell(new Paragraph("Rate", boldSmallFont));
                    cgstRateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(cgstRateCell);

                    PdfPCell cgstAmountCell = new PdfPCell(new Paragraph("Amount", boldSmallFont));
                    cgstAmountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(cgstAmountCell);

                    // Adding Data to the invoice info-----------------
//--------------------------

                    int totalWorkIds = workIds.size();
                    ArrayList<ArrayList<String>> dataDescription = new ArrayList<>();
                    for(int i=1;i<=totalWorkIds;i++) {
                        ArrayList<String> row = new ArrayList<>();
                        //S.no column
                        row.add(String.valueOf(i));
                        // Service Description column
                        row.add(servicesMap.get("service"+i).getDescription());
                        //Hsn Code column
                        row.add(servicesMap.get("service"+i).getHsnCode());
                        //rate inRupee #Fetch from workInfo and service info and use the static method
                        int amount =(int) BillingCalculationMethods.rateInRupee (
                                servicesMap.get("service"+i).getMonthlyRate(),
                                workInfoMap.get("workInfo"+i).getTotalWorkingdays(),
                                workInfoMap.get("workInfo"+i).getWorkingDays());
                        row.add(String.valueOf(amount));
                        //Discount Amount column
                        double discount = workInfoMap.get("workInfo"+i).getDiscount();
                        row.add(String.valueOf((int)discount));
                        //Taxable value column
                        row.add(String.valueOf((int)(amount-discount)));
                        //Sgst Rate column
                        row.add((String.valueOf(gstRate/2) +" %"));
                        //Sgst Amount column
                        row.add(String.valueOf((int)BillingCalculationMethods.calculateSGST((amount-discount),gstRate)));
                        //Cgst Rate column
                        row.add((String.valueOf(gstRate/2) +" %"));
                        //Cgst Amount column
                        row.add(String.valueOf((int)BillingCalculationMethods.calculateCGST((amount-discount),gstRate)));
                        dataDescription.add(row);
                    }

//                    System.out.println();

                    for (ArrayList<String> row : dataDescription) {
                        for (String rowItem : row) {
                            PdfPCell cell = new PdfPCell(new Paragraph(rowItem, normalSmallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            invoiceDescTable.addCell(cell);
                        }
                    }


//---------------------------
//                    String[][] data = {
//                            {"1", "Development", "S62145","50000.0","500","48000","9%","4500","9%","4500" },
//                            {"1", "Designing", "S2145","50000.0","600","48000","9%","4500","9%","4500" },
//                            {"1", "Designing", "S2145","50000.0","1000","48000","9%","4500","9%","4500" },
//                            {"3", "BPO", "SS2145","50000.0","1200","48000","9%","4500","9%","4500" }
//                    };
//
//                    for (String[] row : data) {
//                        for (String rowItems : row) {
//                            PdfPCell cell = new PdfPCell(new Paragraph(rowItems, normalSmallFont));
//                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                            invoiceDescTable.addCell(cell);
//                        }
//                    }

                    //Total Amount Section

                    // Total Calculation
                    int totalAmountSum = 0;
                    for (ArrayList<String> row : dataDescription) {
                        int amount = Integer.parseInt(row.get(3));
                        totalAmountSum = totalAmountSum+amount;
                    }

                    int totalDiscountSum=0;
                    for(ArrayList<String> row : dataDescription){
                        int discountValue = Integer.parseInt(row.get(4));
                        totalDiscountSum +=discountValue;
                    }

                    int totalSgstAmountSum =0;
                    for(ArrayList<String> row : dataDescription){
                        int sgstValue = Integer.parseInt(row.get(7));
                        totalSgstAmountSum = totalSgstAmountSum+sgstValue;
                    }

                    int totalCgstAmountSum =0;
                    for(ArrayList<String> row : dataDescription){
                        int cgstValue = Integer.parseInt(row.get(9));
                        totalCgstAmountSum = totalCgstAmountSum+cgstValue;
                    }

                    int totalTaxableValueSum=0;
                    for(ArrayList<String> row : dataDescription){
                        int taxableValue = Integer.parseInt(row.get(5));
                        totalTaxableValueSum=totalTaxableValueSum+taxableValue;
                    }

                    //Putting data into design

                    PdfPCell totalSectionCell = new PdfPCell(new Paragraph("Total",boldFont));
                    totalSectionCell.setColspan(3);
                    totalSectionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(totalSectionCell);

                    PdfPCell totalRateInRupeeCell = new PdfPCell(new Paragraph(String.valueOf(totalAmountSum),normalSmallFont));
                    totalRateInRupeeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(totalRateInRupeeCell);

                    PdfPCell totalDiscountCell = new PdfPCell(new Paragraph(String.valueOf(totalDiscountSum),normalSmallFont));
                    totalDiscountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(totalDiscountCell);

                    PdfPCell totalTaxableValueCell = new PdfPCell(new Paragraph(String.valueOf(totalTaxableValueSum),normalSmallFont));
                    totalTaxableValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(totalTaxableValueCell);

                    PdfPCell totalSgstRateCell = new PdfPCell(new Paragraph(" ",normalSmallFont));
                    totalSgstRateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(totalSgstRateCell);

                    PdfPCell totalSgstAmountCell = new PdfPCell(new Paragraph(String.valueOf(totalSgstAmountSum),normalSmallFont));
                    totalSgstAmountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(totalSgstAmountCell);

                    PdfPCell totalCgstRateCell = new PdfPCell(new Paragraph(" ",normalSmallFont));
                    totalCgstRateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(totalCgstRateCell);

                    PdfPCell totalCgstAmountCell = new PdfPCell(new Paragraph(String.valueOf(totalCgstAmountSum),normalSmallFont));
                    totalCgstAmountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(totalCgstAmountCell);

                    //Invoice amount in words
                    PdfPCell totalAmountingWordsLabelCell = new PdfPCell(new Paragraph("Total invoice amount in words",
                            boldFont));
                    totalAmountingWordsLabelCell.setColspan(5);
                    totalAmountingWordsLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    invoiceDescTable.addCell(totalAmountingWordsLabelCell);

                    PdfPCell totalAmountBeforeTaxLabelCell = new PdfPCell(new Paragraph("Total amount before Tax ",
                            boldFont));
                    totalAmountBeforeTaxLabelCell.setColspan(3);
                    totalAmountBeforeTaxLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    invoiceDescTable.addCell(totalAmountBeforeTaxLabelCell);

                    PdfPCell totalAmountBeforeTaxCell = new PdfPCell(new Paragraph(String.valueOf(
                            totalAmountSum-totalDiscountSum),
                            normalFont));
                    totalAmountBeforeTaxCell.setColspan(2);
                    totalAmountBeforeTaxCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(totalAmountBeforeTaxCell);

                    int totalAmountAfterTax = totalAmountSum-totalDiscountSum+totalCgstAmountSum+totalSgstAmountSum;

                    PdfPCell totalAmountingWordsCell = new PdfPCell(new Paragraph(IndianCurrencyWordConverter.
                            convertToWords(totalAmountAfterTax)+" Rupee",normalFont));
                    totalAmountingWordsCell.setColspan(5);
                    totalAmountingWordsCell.setRowspan(2);
                    totalAmountingWordsCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    invoiceDescTable.addCell(totalAmountingWordsCell);

                    PdfPCell totalTaxAmountLabelCell = new PdfPCell(new Paragraph("Total Tax Amount: ",
                            boldFont));
                    totalTaxAmountLabelCell.setColspan(3);
                    totalTaxAmountLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    invoiceDescTable.addCell(totalTaxAmountLabelCell);

                    PdfPCell totalTaxAmountCell = new PdfPCell(new Paragraph(String.
                            valueOf(totalCgstAmountSum +totalSgstAmountSum),
                            normalFont));
                    totalTaxAmountCell.setColspan(2);
                    totalTaxAmountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(totalTaxAmountCell);

                    PdfPCell totalAmountAfterTaxLabelCell = new PdfPCell(new Paragraph("Total Amount After Tax: ",
                            boldFont));
                    totalAmountAfterTaxLabelCell.setColspan(3);
                    totalAmountAfterTaxLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    invoiceDescTable.addCell(totalAmountAfterTaxLabelCell);

                    PdfPCell totalAmountAfterTaxCell = new PdfPCell(new Paragraph(String.
                            valueOf(totalAmountAfterTax),normalFont));
                    totalAmountAfterTaxCell.setColspan(2);
                    totalAmountAfterTaxCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(totalAmountAfterTaxCell);

//------------------Footer Section--------------------------------------------------------------------------------------------

                    PdfPCell bankDetailsLabelCell = new PdfPCell(new Paragraph("Bank Details ",
                            boldFont));
                    bankDetailsLabelCell.setColspan(3);
                    bankDetailsLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(bankDetailsLabelCell);

                    PdfPCell blankCell1 = new PdfPCell(new Paragraph(" "));
                    blankCell1.setColspan(2);
                    invoiceDescTable.addCell(blankCell1);


                    PdfPCell gstOnReverseChargeLabelCell = new PdfPCell(new Paragraph("Gst on Reverse Charge: ",
                            boldFont));
                    gstOnReverseChargeLabelCell.setColspan(3);
                    gstOnReverseChargeLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    invoiceDescTable.addCell(gstOnReverseChargeLabelCell);

                    PdfPCell gstOnReverseChargeCell = new PdfPCell(new Paragraph(String.valueOf(gstOnReverseCharge),
                            normalFont));
                    gstOnReverseChargeCell.setColspan(2);
                    gstOnReverseChargeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(gstOnReverseChargeCell);

                    Paragraph accountNumberPara = new Paragraph("Account Number: ", boldFont);
                    accountNumberPara.add(new Chunk(accountNumber+"\n\n",normalFont));
                    accountNumberPara.add(new Chunk("IFSC: ",boldFont));
                    accountNumberPara.add(new Chunk(ifsc+"\n",normalFont));
                    accountNumberPara.add(new Chunk("Bank Name: ",boldFont));
                    accountNumberPara.add(new Chunk(bankName,normalFont));

                    PdfPCell accountNumberLabelCell = new PdfPCell(accountNumberPara);
                    accountNumberLabelCell.setColspan(3);
                    accountNumberLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    invoiceDescTable.addCell(accountNumberLabelCell);

                    // Add Company Seal Image
                    byte[] signatureImageData = companySealImage.getBytes();
                    Image companySealImageInstance = Image.getInstance(signatureImageData);
                    // Set the width and height
                    float stampWidth = 100; // (1 inch = 72 points)
                    float stampHeight =95; //  (1 inch = 72 points)
                    companySealImageInstance.scaleToFit(stampWidth, stampHeight);

                    PdfPCell signatureImageCell = new PdfPCell(companySealImageInstance);
                    signatureImageCell.setColspan(2);
                    signatureImageCell.setRowspan(2);
                    signatureImageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(signatureImageCell);

                    //Certification label
                    Paragraph certificateDeclarationLabelPara = new Paragraph("Certified that the particular given above are true and Correct"+"f\n\n",boldFont);
                    certificateDeclarationLabelPara.add(new Chunk("For: Evision Software Solutions Private Limited",boldSmallFont));
                    PdfPCell certificateDeclarationLabelCell = new PdfPCell(certificateDeclarationLabelPara);
                    certificateDeclarationLabelCell.setColspan(5);
                    certificateDeclarationLabelCell.setRowspan(1);
                    certificateDeclarationLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(certificateDeclarationLabelCell);

                    //Beneficiary details
                    Paragraph beneficiaryDetailsPara = new Paragraph("Beneficiary Details: ",boldFont);
                    beneficiaryDetailsPara.add(new Chunk(beneficiaryName +"\n\n",normalFont));
                    beneficiaryDetailsPara.add(new Chunk("Terms and Conditions",boldFont));
                    PdfPCell beneficiaryDetailsCell =new PdfPCell(beneficiaryDetailsPara);
                    beneficiaryDetailsCell.setColspan(3);
                    beneficiaryDetailsCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    invoiceDescTable.addCell(beneficiaryDetailsCell);

                    //Adding the Authority Signature part

                    byte[] authoritySignImageData = authoritySignImage.getBytes();
                    Image authoritySignImageInstance = Image.getInstance(authoritySignImageData);

                    // Set the width and height of the image as per your desired row and column size
                    float width = 200; // (1 inch = 72 points)
                    float height = 46; //  (1 inch = 72 points)
                    authoritySignImageInstance.scaleToFit(width, height);

                    PdfPCell authoritySignImageCell = new PdfPCell(authoritySignImageInstance);
                    authoritySignImageCell.setColspan(5);
                    authoritySignImageCell.setRowspan(1);
                    authoritySignImageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(authoritySignImageCell);

//---------------------------Last Row---------------------------------------------------------------------------------------

                    Paragraph termAndCondition = new Paragraph("Please pay within 10 days of receiving invoice  ", boldFont);
                    PdfPCell termAndConditionCell = new PdfPCell(termAndCondition);
                    termAndConditionCell.setColspan(3);
                    termAndConditionCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    invoiceDescTable.addCell(termAndConditionCell);

                    PdfPCell companySealLabelCell = new PdfPCell(new Paragraph("Company Seal",boldFont));
                    companySealLabelCell.setColspan(2);
                    companySealLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(companySealLabelCell);

                    PdfPCell authoritySignLabelCell = new PdfPCell(new Paragraph("Authority sign",boldFont));
                    authoritySignLabelCell.setColspan(5);
                    authoritySignLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceDescTable.addCell(authoritySignLabelCell);

                    // Adding table to the Document
                    document.add(invoiceDescTable);

                    // Saving the Document ---------------------------------------------------------------------

                    document.close();
                    LOGGER.info("-------------> Invoice Created <-------------");
                    return new ByteArrayInputStream(out.toByteArray());

                }
                catch (DocumentException de){
                    LOGGER.error("Document Exception Occurred "+de);
                }
                catch(FileSizeLimitExceededException fe){
                    LOGGER.error("Uploaded Image size is greater than 1 Mb",fe);
                }
                catch(Exception e){
                    LOGGER.error("Unexpected Exception occurred "+e);
                }
            return null;
        }
    }
