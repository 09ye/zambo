package com.mobilitychina.zambo.widget;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

public class NumberDecimalTextWatcher implements TextWatcher {

	private boolean locked;
	private EditText editText;
	private int decimal;
	private String oldstr;
	/**
	 * 
	 * @param inputText
	 *            输入框
	 * @param decimal
	 *            小数位数
	 */
	public NumberDecimalTextWatcher(EditText inputText, int decimal) {
		this.editText = inputText;
		this.decimal = decimal;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		this.oldstr= this.editText.getText().toString();
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {


		//Modified by Zhuchao for more user friendly
		if (locked || TextUtils.isEmpty(s)) {
			return;
		}
		locked = true;
		String curStr = s.toString();
		String str = "";
		int count =0;
		int position =-1;
		for (int i = 0; i < curStr.length(); i++) {
			char c = s.charAt(i);
			if ('.' == c){
				count++;
				if (count>1) {
					editText.setText(this.oldstr);
					editText.setSelection(editText.length());
					locked = false;
					return;
				}else {
					position =i;
				}
			}
		}
		if (position == -1) {
			str = curStr.toString();
		} else {
			if (position ==0) {
				str =this.oldstr;
			} else {
				if (curStr.length()-position>3) {
					str = curStr.toString().substring(0, position+3);
				} else {
					str = curStr.toString().substring(0, curStr.toString().length());
				}
			}
		}

		//----------------Menglang's implementation------------		
		/*if (locked || TextUtils.isEmpty(s)) {
			return;
		}
		locked = true;
		String curStr = s.toString();
		String str = "";
		for (int i = 0; i < curStr.length(); i++) {
			char c = s.charAt(i);
			if ('.' != c) {
				str += c;
			}
		}
		if (str.length() <= decimal) {
			String temp = "";
			for (int k = 0; k <= decimal - str.length(); k++) {
				temp += "0";
			}
			str = temp + str;
		}
		str = str.substring(0, str.length() - decimal) + "." + str.substring(str.length() - decimal);
		while(str.length() > 4){
			if(str.charAt(0) == '0'){
				str = str.substring(1);
				continue;
			}
			break;
		}*/
		editText.setText(str);
		editText.setSelection(editText.length());
		locked = false;
	}

}
