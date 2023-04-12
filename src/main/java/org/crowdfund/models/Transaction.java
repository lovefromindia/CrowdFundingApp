package org.crowdfund.models;

public class Transaction {

    private final String productName;
    private final double amount;
    private final double ownership;

    public Transaction(String productName, double amount, double ownership)
    {

        this.productName = productName;
        this.amount = amount;
        this.ownership = ownership;

    }

}
