package base;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;





import java.util.Set;

import org.junit.After;
import org.junit.Before;

import jxl.common.Logger;

import com.alibaba.fastjson.JSONObject;
import com.zlinepay.expand.common.MD5Encrypt;
import com.zlinepay.expand.common.StringUtil;


public class BaseTest {
	protected Logger log = Logger.getLogger(getClass());
//	protected String domain = "http://192.168.6.41:10000/expand/";
	protected String domain = "http://127.0.0.1:8080/expand/";
//	protected String domain = "http://192.168.6.34:7789/";
//	protected String domain = "https://spayment.kklpay.com/";
	@After
	public void destory() {
	}

	@Before
	public void init() {
	}
	
	protected JSONObject getData(String ret) throws Exception {
		issucc(ret);
		try {
			JSONObject json = (JSONObject) JSONObject.parse(ret);
			return json.getJSONObject("data");
		} catch (Exception e) {
			log.error("获取响应数据失败");
			throw e;
		}
	}

	protected boolean issucc(String ret) throws Exception {
		try {
			JSONObject json = (JSONObject) JSONObject.parse(ret);
			return "00".equals(json.getString("code"));
		} catch (Exception e) {
			log.error("解析响应报文失败", e);
			throw e;
		}
	}

	protected String sign(String signFields[], Map<String, String> param, String md5Key) throws Exception {
		String strSrc = orgSignSrc(signFields, param);
		try {
			strSrc += "&KEY=" + md5Key;
			return MD5Encrypt.getMessageDigest(strSrc);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("签名失败");
		}
	}

	/**
	 * 构建签名原文
	 * 
	 * @param signFilds
	 * @param param
	 * @return
	 */
	private String orgSignSrc(String[] signFields, Map<String, String> param) {
		if (signFields != null) {
			Arrays.sort(signFields); // 对key按照 字典顺序排序
		}

		StringBuffer signSrc = new StringBuffer("");
		int i = 0;
		for (String field : signFields) {
			signSrc.append(field);
			signSrc.append("=");
			signSrc.append((StringUtil.isEmpty(param.get(field)) ? "" : param.get(field)));
			// 最后一个元素后面不加&
			if (i < (signFields.length - 1)) {
				signSrc.append("&");
			}
			i++;
		}
		return signSrc.toString();
	}

	/**
	 * 构建签名原文
	 * 
	 * @param signFilds
	 * @param param
	 * @return
	 */
	protected String orgSignSrc(String[] signFields, JSONObject param) {
		if (signFields != null) {
			Arrays.sort(signFields); // 对key按照 字典顺序排序
		}

		StringBuffer signSrc = new StringBuffer("");
		int i = 0;
		for (String field : signFields) {
			signSrc.append(field);
			signSrc.append("=");
			signSrc.append((StringUtil.isEmpty(param.getString(field)) ? "" : param.getString(field)));
			// 最后一个元素后面不加&
			if (i < (signFields.length - 1)) {
				signSrc.append("&");
			}
			i++;
		}
		return signSrc.toString();
	}

	protected void verifyByMD5(JSONObject data, String[] fields, String md5Key) throws Exception {
		String src = orgSignSrc(fields, data);
		src += "&KEY=" + md5Key;
		log.info("src["+src+"]");
		String sign = data.getString("sign");
		log.info("sign["+sign+"]");
		String signCount = "";
		try {
			signCount = MD5Encrypt.getMessageDigest(src);
			log.info("signCount["+signCount+"]");
		} catch (Exception e) {
			throw new Exception("计算签名失败");
		}
		if(!signCount.equals(sign)) {
			throw new Exception("验证签名失败");
		}
		log.info("验签通过");
	}
	
	protected Map<String, String> json2map(JSONObject param) {
		Set<String> fields = param.keySet();
		Map<String, String> map = new HashMap<String, String>();
		for(String f : fields) {
			map.put(f, param.getString(f));
		}
		return map;
	}
	
}
