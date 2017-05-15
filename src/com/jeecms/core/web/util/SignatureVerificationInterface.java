package com.jeecms.core.web.util;




/**
 * @author liyun
 * @version 1.0.*
 * 
 *          验证签名接口
 * 
 */
public interface SignatureVerificationInterface {

	/**
	 * 签名时候用的密钥
	 */
	public final static String SECRET = "733JHas2";

	/**
	 * 公共参数名
	 */

	public final static String METHOD = "method";

	public final static String SESSIONID = "sessionId";

	public final static String APPKEY = "appKey";

	public final static String V = "v";

	public final static String LOCALE = "locale";

	public final static String MESSAGEFORMAT = "messageFormat";

	public final static String SIGN = "sign";
	
	public final static String LEVEL = "level";
	public final String  LOGINNAME="loginName";


	/**
	 * 所有验证通过
	 */
	public final static String SUCCESS = "success";

	/**
	 * 参数名不齐全
	 */
	public final static String KEYPARAMSFAIL = "keyParamsFail";

	/**
	 * 参数名对应的参数值为“”或者为null
	 */
	public final static String VALUEPARAMSFAIL = "valueParamsFail";

	/**
	 * 签名失败
	 */
	public final static String SIGNFAIL = "signFail";

	/**
	 * 参数名不齐JSON字符串
	 */
	public final static String KEYPARAMSFAILJSON = "{\"state\":1}";

	/**
	 * 参数名对应的参数值为“”或者为null JSON字符串
	 */
	public final static String VALUEPARAMSFAILJSON = "{\"state\":2}";

	/**
	 * 签名失败JSON字符串
	 */
	public final static String SIGNFAILJSON = "{\"state\":3}";


}
