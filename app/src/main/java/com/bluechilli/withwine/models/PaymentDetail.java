package com.bluechilli.withwine.models;

import org.parceler.Parcel;

/**
 * Created by monishi on 5/02/15.
 */
@Parcel
public class PaymentDetail extends CreditCardDetail {

    public String getExpiry() {

        String month = org.apache.commons.lang3.StringUtils.leftPad(String.format("%d", this.expiryMonth), 2, "0");
        String year = String.format("%d", this.expiryYear);

        String replacedMonth = month.replaceAll("(.*)", "X");
        String replacedYear = year.replaceAll("(.*)", "X");


        return String.format("%s/%s", replacedMonth, replacedYear);

    }

}
