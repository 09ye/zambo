package com.mobilitychina.zambo.service.message.bus;

import com.mobilitychina.zambo.service.message.BaseMsgInfo;

 public interface BusDelegate {
 public Boolean processMsg(BaseMsgInfo msg);
}
