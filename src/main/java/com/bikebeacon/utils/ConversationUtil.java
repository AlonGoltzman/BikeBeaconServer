package com.bikebeacon.utils;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

import static com.bikebeacon.utils.AssetsUtil.FileContentDistributer;
import static com.bikebeacon.utils.Constants.CONVERSATION_WORKSPACE_ID;

public class ConversationUtil {

    private ConversationService convo;

    public ConversationUtil() {
        FileContentDistributer distributer = AssetsUtil.load("conversation.creds").extractContent();
        String username = distributer.getLine(2);
        String password = distributer.getLine(3);
        convo = new ConversationService("2017-05-26", username, password);
    }

    public MessageResponse tellJerry(String input) {
        MessageRequest request = new MessageRequest.Builder().inputText(input).build();
        return convo.message(CONVERSATION_WORKSPACE_ID, request).execute();
    }

}
