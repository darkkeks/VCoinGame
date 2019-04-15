package ru.darkkeks.vcoin.game;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.queries.messages.MessagesSendQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.darkkeks.vcoin.game.api.Transaction;
import ru.darkkeks.vcoin.game.vk.VkUtil;
import ru.darkkeks.vcoin.game.vk.keyboard.Keyboard;

public abstract class GameSession {

    private static final Logger logger = LoggerFactory.getLogger(GameSession.class);

    private AppContext context;
    private int chatId;

    protected GameSession(AppContext context, int chatId) {
        this.context = context;
        this.chatId = chatId;
    }

    public void sendMessage(String text, Keyboard keyboard) {
        sendMessage(text, null, keyboard);
    }

    public void sendMessage(String text, String attachment, Keyboard keyboard) {
        MessagesSendQuery query = getContext().getVk().messages().send(getContext().getActor());
        query.message(text);
        query.peerId(getChatId());
        query.randomId(VkUtil.randomId());
        if(keyboard != null) {
            query.unsafeParam("keyboard", getContext().getVk().getGson().toJson(keyboard));
        }
        if(attachment != null) {
            query.attachment(attachment);
        }
        try {
            query.execute();
        } catch (ApiException | ClientException e) {
            logger.error("Api error", e);
        }
    }

    public abstract void acceptMessage(Message message);

    public abstract void acceptPayment(Transaction transaction);

    public AppContext getContext() {
        return context;
    }

    public int getChatId() {
        return chatId;
    }
}
