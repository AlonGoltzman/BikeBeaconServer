package com.bikebeacon.pojo;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum CCHResponders {

    TTS(1),
    STT(2),
    CONVERSATION(3),
    COMPRESSION(4),
    FCM(5),
    CLOUDCONVERT(6),
    MAILINGLIST(7);

    private int id;

    CCHResponders(int num) {
        id = num;
    }

    @Contract(pure = true)
    public int getId() {
        return id;
    }

    @Contract("null -> fail")
    @NotNull
    public static CCHResponders getResponder(Integer value) {
        for (CCHResponders responder : values()) {
            if (responder.id == value)
                return responder;
        }
        //noinspection ConstantConditions
        return null;
    }
}
