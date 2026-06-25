package com.bookNDrive.user_service.address;

import com.bookNDrive.user_service.entities.Address;

public class AddressTestBuilder {

    private String addressLine1 = "938 avenue des platanes";
    private String city = "Lattes";
    private String postalCode = "34970";
    private String country = "France";

    public static AddressTestBuilder anAddress() {
        return new AddressTestBuilder();
    }

    public AddressTestBuilder withAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
        return this;
    }

    public AddressTestBuilder withCity(String city) {
        this.city = city;
        return this;
    }

    public AddressTestBuilder withPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public AddressTestBuilder withCountry(String country) {
        this.country = country;
        return this;
    }

    public Address build() {
        var address = new Address();
        address.setPostalCode(postalCode);
        address.setCountry(country);
        address.setAddressLine1(addressLine1);
        address.setCity(city);
        return address;
    }

}
