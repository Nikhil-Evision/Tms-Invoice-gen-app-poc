package com.tms.invoiceapp.utility;

public class BillingCalculationMethods {

    //Method for the Gst calculation
    public static double calculateSGST(double price, double gstRate)
    {
        return (price * (gstRate/2)) / 100.0;
    }

    public static double calculateCGST(double price, double gstRate)
    {
        return (price * (gstRate/2)) / 100.0;
    }

    public static double calculateIGST(double price, double gstRate)
    {
        return (price * (gstRate)) / 100.0;
    }


    //Method for Monthly Rate
    public static double rateInRupee(double mothlyRate,double totalWorkingDays,double actualWorkingDays)
    {
        return ((mothlyRate)/totalWorkingDays)*actualWorkingDays;
    }




}
