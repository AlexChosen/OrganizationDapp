package com.pingan.chain.common;


public enum StateCode {
	/**
	 *  操作成功
	 */
	Success("Success", 200, "操作成功"),
	/**
	 *  操作失败
	 */
	Fail("Fail", 401, "操作失败"),
	/**
	 *  请求参数错误
	 */
	RequestParamError("RequestParamError",402,"请求参数错误"),
	/**
	 * 请求参数不能为空
	 */
	RequestParamNUll("RequestParamNUll",403,"请求参数不能为空"),
	/**
	 *  查无数据  查询结果为空
	 */
	QueryResultIsNUll("QueryResultIsNUll",404,"查无数据"),
	/**
	 *   参数失效  过期
	 */
	RequestParamInvalid("RequestParamInvalid",405,"参数已过期"),
	/**
	 *  参数类型错误
	 */
	RequestParamTypeError("RequestParamTypeError",406,"参数类型错误"),
	/**
	 *  参数格式错误
	 */
	RequestParamFormatError("RequestParamFormatError",407,"参数格式错误"),
	/**
	 * 系统内部错误
	 */
	InternalError("InternalError",500,"系统内部错误")
	;
	private String name;
	private int code;
	private String descript;
	
	private StateCode(String name, int code, String descript) {
		this.name = name;
		this.code = code;
		this.descript = descript;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return String.valueOf(code);
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDescript() {
		return descript;
	}

	public void setDescript(String descript) {
		this.descript = descript;
	}
	
}
