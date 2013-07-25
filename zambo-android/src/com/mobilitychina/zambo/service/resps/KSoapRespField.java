package com.mobilitychina.zambo.service.resps;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Kris Lau (kris.lau@sap.com) KSOAP annotation
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface KSoapRespField {

	String name();

}
