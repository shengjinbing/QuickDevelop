package com.modesty.quickdevelop.utils.image.luban;

import java.io.File;

public interface OnCompressListener {

  /**
   * Fired when the compression is started, override to handle in your own code
   */
  void onStart();

  /**
   * Fired when MVPActivityModelImpl compression returns successfully, override to handle in your own code
   */
  void onSuccess(File file);

  /**
   * Fired when MVPActivityModelImpl compression fails to complete, override to handle in your own code
   */
  void onError(Throwable e);
}
