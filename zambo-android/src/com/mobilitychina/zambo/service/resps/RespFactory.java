package com.mobilitychina.zambo.service.resps;

/**
 * @author Kris Lau (kris.lau@sap.com) convert object to/from response through
 *         nongfu's webservice
 */
public class RespFactory {
	public static Implementation DEFAULT_SERVICE = Implementation.KSoapResp;

	public enum Implementation {
		KSoapResp,
	}

	public static IResp getService(Implementation impl) {
		switch (impl) {
		case KSoapResp:
			return new KSoapResp();

		}
		return null;
	}

	public static IResp getService() {
		return getService(DEFAULT_SERVICE);
	}
}
