package com.mobilitychina.zambo.service.resps;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import com.mobilitychina.util.Log;

/**
 * @author Kris Lau (kris.lau@sap.com) KSOAP response typical response:
 *         anyType{category_id=23; category_name=农夫果园100%380ML橙普通装1*12;
 *         category_sort=100%农夫果园; category_unit=�? } TODO: toResp is
 *         unimplemented
 */

public class KSoapResp implements IResp {

	@SuppressWarnings("rawtypes")
	@Override
	public <T> List<T> fromResp(Class<T> c, Object resp) {
		if (resp == null)
			return null;

		HashMap<String, String> fieldMap = new HashMap<String, String>();
		List<T> objList = new ArrayList<T>();

		Class cls = c.getSuperclass() == Object.class ? c : c.getSuperclass();
		Field fields[] = cls.getDeclaredFields();

		for (Field f : fields) { // loop all fields
			Annotation[] annos = f.getDeclaredAnnotations();
			for (Annotation a : annos) { // loop through annonations
				if (a != null && (a instanceof KSoapRespField)) { // if found,
																	// store it
					fieldMap.put(((KSoapRespField) a).name(), f.getName());
				}
			}
		}

		int n = ((SoapObject) resp).getPropertyCount();
		for (int i = 0; i < n; i++) {
			try {
				String element = ((SoapObject) resp).getProperty(i).toString();
				Log.i("ksoapResp",c.getName()+">>>"+element);
				T obj = c.newInstance();
				for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
					String key = entry.getKey();
					String val = entry.getValue();

					Field field = null;
					try {
						field = cls.getDeclaredField(val);
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (field != null) {
						String tag = key + "=";
						if (element.indexOf(tag) == -1)
							continue;

						String str = element.substring(element.indexOf(tag) + tag.length(),
								element.indexOf(";", element.indexOf(tag)));

						if (str.length() > 0) { // TODO: add addition type
												// checks?
							field.setAccessible(true);

							if (field.getType() == String.class) // Field is a
																	// String
																	// field
							{
								if (!str.equalsIgnoreCase("null"))
									field.set(obj, str);
							} else if (field.getType() == int.class) { // Field
																		// is an
																		// int
																		// field
								try {
									field.setInt(obj, Integer.valueOf(str));
								} catch (NumberFormatException e) {
									field.setInt(obj, 0);
								}
							} else if (field.getType() == long.class) { // Field
																		// is an
																		// int
																		// field
								try {
									field.setLong(obj, Long.valueOf(str));
								} catch (NumberFormatException e) {
									field.setLong(obj, 0);
								}
							} else if (field.getType() == java.lang.Long.class) { // Field
								// is an
								// int
								// field
								if(!str.equals("null")){
								try {
									field.setLong(obj, Long.valueOf(str));
								} catch (Exception e) {
									field.setFloat(obj, 0);
								}
								}
							} else if (field.getType() == double.class) { // Field
																			// is
																			// a
																			// double
																			// field
								try {
									field.setDouble(obj, Double.valueOf(str));
								} catch (NumberFormatException e) {
									field.setDouble(obj, 0.0);
								}
							}
						}
					}
				} // end for field loop
				objList.add(obj);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return objList;
	}

	@Override
	public Object toResp(Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

}
