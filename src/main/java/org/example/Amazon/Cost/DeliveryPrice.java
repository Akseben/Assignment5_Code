package org.example.Amazon.Cost;

import org.example.Amazon.Item;

import java.util.List;

public class DeliveryPrice implements PriceRule {
    @Override
    public double priceToAggregate(List<Item> cart) {

        int totalItems = cart.size();

        if(totalItems == 0)
            return 0;
        if(totalItems <= 3) // changed to reduce redundancy and improve test coverage
            return 5;
        if(totalItems <= 10) // changed to reduce redundancy and improve test coverage
            return 12.5;

        return 20.0;
    }
}
