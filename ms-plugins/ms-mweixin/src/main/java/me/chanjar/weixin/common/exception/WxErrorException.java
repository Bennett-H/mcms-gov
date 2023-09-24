/**
 * Copyright (c) 2012-present 铭软科技(mingsoft.net)
 * 本软件及相关文档文件（以下简称“软件”）的版权归 铭软科技 所有
 * 遵循 铭软科技《服务协议》中的《保密条款》
 */





package me.chanjar.weixin.common.exception;

import me.chanjar.weixin.common.error.WxError;

public class WxErrorException
  extends Exception
{
  private static final long serialVersionUID = -6357149550353160810L;
  private WxError error;
  
  public WxErrorException(WxError error)
  {
    super(error.toString());
    this.error = error;
  }
  
  public WxErrorException(WxError error, Throwable cause)
  {
    super(error.toString(), cause);
    this.error = error;
  }
  
  public WxError getError()
  {
    return this.error;
  }
}
