package com.jstyle.blesdk2025.callback;


import com.jstyle.blesdk2025.model.Device;

public interface OnScanResults {
  void Success(Device date);
  void Fail(int code);
}
