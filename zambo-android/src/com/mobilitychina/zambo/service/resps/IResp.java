package com.mobilitychina.zambo.service.resps;

import java.util.List;

/**
 * @author Kris Lau (kris.lau@sap.com) interface to a response from Nongfu's
 *         webservice responses should implement toResp and fromResp toResp
 *         converts an object to a response to be consumed by webservice
 *         fromResp creates a list of objects from response received from
 *         webservice
 */
public interface IResp {
	Object toResp(Object obj);

	<T> List<T> fromResp(Class<T> c, Object resp);
}
