// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.bayer.gifts.process.sys.auth2;

import java.util.Date;

class StateData {
    private String nonce;
    private Date expirationDate;

    StateData(String nonce, Date expirationDate) {
        this.nonce = nonce;
        this.expirationDate = expirationDate;
    }

    String getNonce() {
        return nonce;
    }

    Date getExpirationDate() {
        return expirationDate;
    }
}