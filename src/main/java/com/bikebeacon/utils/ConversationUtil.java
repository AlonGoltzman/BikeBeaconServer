package com.bikebeacon.utils;

import com.bikebeacon.cch.Case;
import com.bikebeacon.pojo.BaseUtilClass;
import com.bikebeacon.pojo.CCHResponders;
import com.bikebeacon.pojo.TaskCompletionListener;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.bikebeacon.utils.AssetsUtil.FileContentDistributor;
import static com.bikebeacon.pojo.Constants.CONVERSATION_WORKSPACE_ID;
import static com.bikebeacon.utils.GeneralUtils.addObjects;

public class ConversationUtil extends BaseUtilClass implements Runnable {

    private static final int RESPONDER_CODE = CCHResponders.CONVERSATION.getId();

    private ConversationService convo;
    private Case owner;
    private Map<String, Object> context;
    private String input;

    public ConversationUtil(TaskCompletionListener listener, Case ownerCase) {
        super(listener);
        FileContentDistributor distributer = AssetsUtil.load("conversation.creds").extractContent();
        String username = distributer.getLine(2);
        String password = distributer.getLine(3);
        convo = new ConversationService("2017-05-26", username, password);
        owner = ownerCase;
        setRunnable(this);
    }

    @Override
    public void failed(@Nullable Object reason) {
        if (reason != null && reason instanceof Exception)
            listener.onFailed(RESPONDER_CODE, (Throwable) reason);
        else
            listener.onFailed(RESPONDER_CODE, null);
    }

    @Override
    public void success(@Nullable Object... results) {
        if (results == null)
            listener.onSuccess(RESPONDER_CODE, owner);
        else {
            listener.onSuccess(RESPONDER_CODE, addObjects(owner, results));
        }
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void reset() {
        setRunnable(this);
    }

    @Override
    public void run() {
        try {
            MessageRequest request;
            if (context != null)
                request = new MessageRequest.Builder().inputText(input).context(context).build();
            else
                request = new MessageRequest.Builder().inputText(input).build();
            MessageResponse response = convo.message(CONVERSATION_WORKSPACE_ID, request).execute();
            setContext(response.getContext());
            success(response);
        } catch (Exception e) {
            e.printStackTrace();
            failed(e);
        }
    }

}
