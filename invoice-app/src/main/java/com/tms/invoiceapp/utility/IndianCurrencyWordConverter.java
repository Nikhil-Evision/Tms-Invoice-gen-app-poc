package com.tms.invoiceapp.utility;

public class IndianCurrencyWordConverter {
    private static final String[] ones = {
            "", "One ", "Two ", "Three ", "Four ", "Five ", "Six ", "Seven ", "Eight ", "Nine "
    };
    private static final String[] tens = {
            "", "", "Twenty ", "Thirty ", "Forty ", "Fifty ", "Sixty ", "Seventy ", "Eighty ", "Ninety "
    };
    private static final String[] teens = {
            "Ten ", "Eleven ", "Twelve ", "Thirteen ", "Fourteen ", "Fifteen ", "Sixteen ", "Seventeen ", "Eighteen ", "Nineteen "
    };

    public static String convertToWords(int value) {
        if (value == 0) {
            return "Zero ";
        }

        if (value < 0 || value > 999999999) {
            throw new IllegalArgumentException("Input value should be between 0 and 999999999");
        }

        StringBuilder words = new StringBuilder();

        // Crores
        if (value >= 10000000) {
            words.append(convertToWords(value / 10000000)).append("Crore ");
            value %= 10000000;
        }

        // Lakhs
        if (value >= 100000) {
            words.append(convertToWords(value / 100000)).append("Lakh ");
            value %= 100000;
        }

        // Thousands
        if (value >= 1000) {
            words.append(convertToWords(value / 1000)).append("Thousand ");
            value %= 1000;
        }

        // Hundreds
        if (value >= 100) {
            words.append(ones[value / 100]).append("Hundred ");
            value %= 100;
        }

        // Tens and Ones
        if (value >= 20) {
            words.append(tens[value / 10]);
            value %= 10;
        } else if (value >= 10) {
            words.append(teens[value - 10]);
            value = 0;
        }

        if (value > 0) {
            words.append(ones[value]);
        }

        return words.toString();
    }

//    public static void main(String[] args) {
//        int amount = 50000000;
//        String words = convertToWords(amount);
//        System.out.println(words);
//    }
}
