package com.bluechilli.withwine.models;

import org.parceler.Parcel;

/**
 * Created by monishi on 16/02/15.
 */
@Parcel
public class CreditCardDetail {

    public String cardName;
    public String cardNumber;
    public int expiryMonth;
    public int expiryYear;
    public String CCV;
}
