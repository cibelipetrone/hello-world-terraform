package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class FuncaoUmHandler implements RequestHandler<Object, String> {

    public String handleRequest(Object input, Context context){
        return "Hello, Terraform";
    }
}
