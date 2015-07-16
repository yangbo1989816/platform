package auth;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import base.BaseTest;

import com.alibaba.fastjson.JSONObject;
import com.util.HttpUtilKeyVal;
import com.zlinepay.expand.common.DateUtil;
import com.zlinepay.expand.common.StringUtil;
import com.zlinepay.expand.server.controller.auth.vo.OrderVo;
import com.zlinepay.expand.server.controller.auth.vo.PayVo;
import com.zlinepay.expand.server.controller.auth.vo.SmsVo;

/**
 * 
 * 
 * @Classname：AuthControllerTest
 * @Description： 认证支付接口测试
 * @author：xiongyuanming
 * @mender：
 * @date：2015年6月23日 下午2:40:02
 * 
 * @version V1.0
 * 
 */
public class AuthControllerTest extends BaseTest {
	private String md5Key = "123456ADSEF";
	private String merchantCode = "1000000001";
	private String phone = "13980097545";
	private String cardNo = "6212264402027791882";
	private String outUserId = "0123456789AA";
	private String holder = "周文娟";
	private String idType = "01";
	private String idNo = "513902198902015860";
	private String expire = "";
	private String cvn2 = "";

	@Test
	public void TestCreatOrder() throws Exception {
		String nonceStr = StringUtil.getRandomNum(32);
		String outOrderId = StringUtil.getRandomNum(32);
		String payNotifyUrl = "";
		Date now = new Date();
		String ct = DateUtil.formatDate2(now);
		DateUtil.delayMinute(now, 30);
		String et = DateUtil.formatDate2(now);
		createOrder(merchantCode, nonceStr, outUserId, outOrderId, 1l, "test", "test", payNotifyUrl, ct, et);
	}

	@Test
	public void TestSms() throws Exception {
		// 步骤一:创建订单
		String nonceStr = StringUtil.getRandomNum(32);
		String outOrderId = StringUtil.getRandomNum(32);
		String payNotifyUrl = "";
		Date now = new Date();
		String ct = DateUtil.formatDate2(now);
		DateUtil.delayMinute(now, 30);
		String et = DateUtil.formatDate2(now);
		String orderId = createOrder(merchantCode, nonceStr, outUserId, outOrderId, 1l, "test", "test", payNotifyUrl,
				ct, et);
		// 步骤二:发送短信
		sms(merchantCode, orderId, phone, cardNo);
	}

	@Test
	public void TestPay() throws Exception {
		String orderId = "150522000052";
		String smsCode = "983752";
		pay(orderId, smsCode);
	}

	private void pay(String orderId, String smsCode) throws Exception {
		String url = "auth/pay.do";
		// 必填 { "merchantCode", "orderId", "cardNo", "phone", "holder",
		// "idType", "idNo", "smsCode", "sign" };
		Map<String, String> map = new HashMap<String, String>();
		map.put("merchantCode", merchantCode);
		map.put("orderId", orderId);
		map.put("cardNo", cardNo);
		map.put("phone", phone);
		map.put("holder", holder);
		map.put("idType", idType);
		map.put("idNo", idNo);
		map.put("expire", expire);
		map.put("cvn2", cvn2);
		map.put("smsCode", smsCode);
		// 计算签名
		map.put("sign", sign(new PayVo().configSign(), map, md5Key));
		String ret = HttpUtilKeyVal.doPost(domain + url, map);
		log.info("支付响应:" + ret);
		JSONObject data = getData(ret);
		// 验证响应报文签名
		verifyByMD5(data, new PayVo().configRespSign(), md5Key);
	}

	private String createOrder(String merchantCode, String nonceStr, String outUserId, String outOrderId,
			Long totalAmount, String goodsName, String goodsExplain, String payNotifyUrl, String orderCreateTime,
			String lastPayTime) throws Exception {
		String signFields[] = { "merchantCode", "nonceStr", "outUserId", "outOrderId", "totalAmount",
				"orderCreateTime", "lastPayTime" };
		String url = "auth/createOrder.do";

		Map<String, String> map = new HashMap<String, String>();
		map.put("merchantCode", merchantCode);
		map.put("nonceStr", nonceStr);
		map.put("outUserId", outUserId);
		map.put("outOrderId", outOrderId);
		map.put("totalAmount", totalAmount + "");
		map.put("goodsName", goodsName);
		map.put("goodsExplain", goodsExplain);
		map.put("payNotifyUrl", payNotifyUrl);
		map.put("orderCreateTime", orderCreateTime);
		map.put("lastPayTime", lastPayTime);

		map.put("sign", sign(signFields, map, md5Key));
		String ret = HttpUtilKeyVal.doPost(domain + url, map);
		// String ret = HttpWeb.post(domain + url, map);
		log.info("创建订单响应:" + ret);
		JSONObject data = getData(ret);
		// 验证响应报文签名
		verifyByMD5(data, new OrderVo().configRespSign(), md5Key);
		String orderId = data.getString("orderId");
		if (StringUtil.isEmpty(orderId)) {
			log.error("响应报文订单号为空:" + orderId);
		}
		return orderId;
	}

	private void sms(String merchantCode, String orderId, String phone, String cardNo) throws Exception {
		String url = "auth/sms.do";

		Map<String, String> map = new HashMap<String, String>();
		map.put("merchantCode", merchantCode);
		map.put("orderId", orderId);
		map.put("phone", phone);
		map.put("cardNo", cardNo);

		map.put("sign", sign(new SmsVo().configSign(), map, md5Key));
		String smsRet = HttpUtilKeyVal.doPost(domain + url, map);
		log.info("获取短信验证码响应:" + smsRet);
		// 验证是否发送成功
		issucc(smsRet);
	}
}
