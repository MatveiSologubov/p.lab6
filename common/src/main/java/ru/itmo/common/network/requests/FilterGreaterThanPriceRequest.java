package ru.itmo.common.network.requests;

public class FilterGreaterThanPriceRequest extends Request {
    private final float price;

    public FilterGreaterThanPriceRequest(float price) {
        super("filter_greater_than_price");
        this.price = price;
    }

    public float getPrice() {
        return price;
    }
}
