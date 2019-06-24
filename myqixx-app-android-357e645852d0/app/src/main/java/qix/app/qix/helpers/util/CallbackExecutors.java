package qix.app.qix.helpers.util;

import android.os.Handler;
import android.os.Looper;

public final class CallbackExecutors {

  private final Handler handler;

  public static CallbackExecutors createDefault() {
    return new CallbackExecutors(new Handler(Looper.getMainLooper()));
  }

  private CallbackExecutors(Handler handler) {
    this.handler = handler;
  }

  public Handler handler() {
    return handler;
  }
}
