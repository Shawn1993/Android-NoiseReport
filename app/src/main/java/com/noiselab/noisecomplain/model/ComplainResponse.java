package com.noiselab.noisecomplain.model;

/**
 * Created by shawn on 24/3/2016.
 */
public class ComplainResponse {
    public static final transient int RESULT_SUCCESS = 1;
    public static final transient int RESULT_ERROR = 0;

    public String formId;
    public Integer result;

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }
}
