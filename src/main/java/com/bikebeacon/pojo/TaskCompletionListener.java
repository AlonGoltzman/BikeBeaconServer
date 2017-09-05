package com.bikebeacon.pojo;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public interface TaskCompletionListener {

    void onSuccess(@NotNull int responder, @Nullable Object... replyParams);

    void onFailed(@NotNull int responder, @Nullable Throwable reason);
}
