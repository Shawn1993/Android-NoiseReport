package com.noiselab.noisecomplain.utility;

import com.noiselab.noisecomplain.model.ComplainForm;

/**
 * Created by shawn on 28/3/2016.
 */
public class ComplainFormManager {

    private static ComplainForm form = null;

    public static void clear() {
        form = null;
    }

    public static ComplainForm getRequestForm() {
        if (form == null) {
            form = new ComplainForm();
        }
        return form;
    }
}
