package payment;

import java.util.Map;

import org.junit.Test;

import base.BaseTest;

import com.alibaba.fastjson.JSONObject;
import com.util.HttpUtilKeyVal;
import com.zlinepay.expand.common.BaseVo;
import com.zlinepay.expand.common.StringUtil;
import com.zlinepay.expand.server.controller.payment.vo.MerBalanceVo;
import com.zlinepay.expand.server.controller.payment.vo.PayToBankCardVo;
import com.zlinepay.expand.server.controller.payment.vo.QueryStateVo;
import com.zlinepay.expand.server.controller.payment.vo.WithdrawVo;

public class PaymentTest extends BaseTest {
	String merchantCode = "1000000001";
	String md5Key = "123456ADSEF";
//	String outUserId = "123456789";
	String intoCardName = "粟霄";
	String intoCardNo = "6222024402055182806";
	String bankCode = "102100099996"; // "102100099996";// "102";//
										// "102651022602";
	String bankName = "成都市石油路工商银行支行";
	String intoCardType = "2"; // 1-对公 2-对私
	String remark = "测试出款";
	String type = "03"; // 03-非实时付款到银行卡;04-实时付款到银行卡

	/**
	 * 
	 * @Title: TestPayToCard
	 * @Description: 付款到银行卡测试用例 (入口)
	 * @throws Exception
	 * 
	 * @since 1.0
	 */
	@Test
	public void TestPayToCard() throws Exception {
		String nonceStr = StringUtil.getRandomNum(32);
		String outOrderId = StringUtil.getRandomNum(32);
		Long totalAmount = 1l;
		String sign = "";
		PayToBankCardVo vo = new PayToBankCardVo(merchantCode, nonceStr, outOrderId, totalAmount,
				intoCardNo, intoCardName, intoCardType, bankCode, bankName, remark, sign, type);

		send(vo, 1);
	}

	/**
	 * 
	 * @Title: withdraw
	 * @Description: 商户提现测试用例(入口)
	 * @throws Exception
	 * 
	 * @since 1.0
	 */
	@Test
	public void testWithdraw() throws Exception {
		String nonceStr = StringUtil.getRandomNum(32);
		String outOrderId = StringUtil.getRandomNum(32);
		Long totalAmount = 1l;
		String sign = "";
		WithdrawVo vo = new WithdrawVo(merchantCode, nonceStr, outOrderId, totalAmount, remark, sign);

		send(vo, 2);
	}

	/**
	 * 
	 * @throws Exception
	 * @Title: queryState
	 * @Description: 查询出款状态测试用例
	 * 
	 * @since 1.0
	 */
	@Test
	public void testQueryState() throws Exception {
		String nonceStr = StringUtil.getRandomNum(32);
		String outOrderId = "68917897137638941169649421735679";
		String sign = "";
		QueryStateVo vo = new QueryStateVo(merchantCode, nonceStr, outOrderId, sign);

		send(vo, 3);
	}
	
	/**
	 * 
	 * @Title: merBalance 
	 * @Description: 查询商户余额 
	 * @throws Exception
	 * 
	 * @since  1.0
	 */
	@Test
	public void merBalance() throws Exception {
		String nonceStr = StringUtil.getRandomNum(32);
		String sign = "";
		MerBalanceVo vo = new MerBalanceVo(merchantCode, nonceStr, sign);

		send(vo, 4);
	}
	/**
	 * 
	 * @Title: queryState
	 * @Description:
	 * @param vo
	 *            - 接口对象
	 * @param interfaces
	 *            - 接口参数1-付款，2-提现，3-查询出款状态，4-查询商户余额
	 * @throws Exception
	 * 
	 * @since 1.0
	 */
	public void send(BaseVo vo, int interfaces) throws Exception {
		String url = "";
		String desp = "";
		switch (interfaces) {
		case 1:
			url = "payment/payment.do";
			desp = "付款到银行卡";
			break;
		case 2:
			url = "payment/withdraw.do";
			desp = "商户提现";
			break;
		case 3:
			url = "payment/queryState.do";
			desp = "查询出款状态";
			break;
		case 4:
			url = "payment/merBalance.do";
			desp = "查询商户账户余额";
			break;
		default:
			break;
		}

		JSONObject param = (JSONObject) JSONObject.toJSON(vo);
		Map<String, String> map = json2map(param);
		map.put("sign", sign(vo.configSign(), map, md5Key));

		String ret = HttpUtilKeyVal.doPost(domain + url, map);
		log.info(desp + "响应:" + ret);
		JSONObject data = getData(ret);
		// 验证响应报文签名
		verifyByMD5(data, vo.configRespSign(), md5Key);
	}
}
