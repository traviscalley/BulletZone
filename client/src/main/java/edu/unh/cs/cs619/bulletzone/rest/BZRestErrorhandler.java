package edu.unh.cs.cs619.bulletzone.rest;

import org.androidannotations.annotations.EBean;
import org.androidannotations.rest.spring.annotations.RestService;
import org.androidannotations.rest.spring.api.RestClientHeaders;
import org.androidannotations.rest.spring.api.RestErrorHandler;
import org.springframework.core.NestedRuntimeException;

/**
 * Created by simon on 10/3/14.
 */
@EBean
public class BZRestErrorhandler implements RestErrorHandler {

    @Override
    public void onRestClientExceptionThrown(NestedRuntimeException e) {
        e.printStackTrace();
    }
}
