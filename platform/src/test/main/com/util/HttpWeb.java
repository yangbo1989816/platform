package com.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HttpWeb {
	public static String post(String urlAddr, Map<String, String> params) {
		InputStream l_urlStream = null;
		URLConnection connection = null;
		OutputStreamWriter out = null;
		try {
			URL url = new URL(urlAddr);
			connection = url.openConnection();
			connection.setDoOutput(true);
			out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
			out.write(requestData(params)); // 向页面传递数据。post的关键所在！
			out.flush();
			out.close();
			// 一旦发送成功，用以下方法就可以得到服务器的回应：
			String sCurrentLine;
			String sTotalString;
			sCurrentLine = "";
			sTotalString = "";

			l_urlStream = connection.getInputStream();
			// 传说中的三层包装阿！
			BufferedReader l_reader = new BufferedReader(new InputStreamReader(l_urlStream));
			while ((sCurrentLine = l_reader.readLine()) != null) {
				sTotalString += sCurrentLine;
			}
			return sTotalString;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
				}
			}
			if (connection != null) {
			}
		}

	}

	private static String requestData(Map<String, String> params) {
		String str = "";
		if (params != null && !params.isEmpty()) {
			Set<String> keys = params.keySet();
			Iterator<String> it = keys.iterator();
			boolean first = true;
			while (it.hasNext()) {
				String key = it.next();
				String val = params.get(key);
				if (first) {
					first = false;
				} else {
					str += "&";
				}
				str += key + "=" + val;
			}
		}
		return str;
	}

	public static void main(String[] args) throws IOException {
		
	}

}
