package com.lionsquare.comunidadkenna.api;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by EDGAR ARANA on 03/08/2017.
 */

public class RBParseo {

    public static RequestBody parseoText(String txt) {
        return RequestBody.create(MediaType.parse("text/plain"), txt);
    }
}
