package ru.darkkeks.vcoin.game;

import com.vk.api.sdk.callback.CallbackApi;
import com.vk.api.sdk.objects.messages.Message;

public class GameBotCallback<T extends GameSession> extends CallbackApi {

    private Game<T> game;
    private AppContext context;
    private String secret;

    public GameBotCallback(AppContext context, Game<T> game, String secret) {
        this.game = game;
        this.context = context;
        this.secret = secret;
    }

    @Override
    public void messageNew(Integer groupId, String secret, Message message) {
        if(!secret.equals(this.secret)) return;
        context.getExecutorService().submit(() -> {
            try {
                game.getSession(message.getPeerId()).acceptMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
