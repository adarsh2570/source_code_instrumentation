package com.uic.ahegde5.instrumentation.utility;


import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class TemplateClass {

    final static Logger logger = Logger.getLogger(TemplateClass.class.getName());

    public static void instrum(int lineNo, String operation, PairClass... pairs){
        PropertyConfigurator.configure("log4j.properties");

        try {
            String s = ("Line: " + String.valueOf(lineNo) + ", " + operation + " ");
            if (null != pairs && pairs.length > 0)
                for (PairClass pair : pairs) {
                    s += (", " + pair.toString());
                }
            logger.info(s);
        }
        catch (Exception e){}
    }

    public static String valueOf(Object input){
        if(null != input){
            return String.valueOf(input);
        }
        return "null";
    }


}
