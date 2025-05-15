package com.product.price.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductPriceDto {
    @JsonProperty("productPrice")
    private String productPrice;
    @JsonProperty("productTitle")
    private String productTitle;

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }
}
