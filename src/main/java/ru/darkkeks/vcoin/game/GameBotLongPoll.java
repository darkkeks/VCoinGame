package ru.darkkeks.vcoin.game;

import com.vk.api.sdk.callback.longpoll.CallbackApiLongPoll;
import com.vk.api.sdk.callback.objects.group.CallbackGroupJoin;
import com.vk.api.sdk.callback.objects.group.CallbackGroupLeave;
import com.vk.api.sdk.objects.messages.Message;

public class GameBotLongPoll<T extends GameSession> extends CallbackApiLongPoll {

    private Game<T> game;
    private AppContext context;

    public GameBotLongPoll(AppContext context, Game<T> game) {
        super(context.getVk(), context.getActor());
        this.game = game;
        this.context = context;
    }

    @Override
    public void messageNew(Integer groupId, Message message) {
        context.getExecutorService().submit(() -> {
            try {
                game.getSession(message.getPeerId()).acceptMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void groupLeave(Integer groupId, CallbackGroupLeave message) {
        context.getFollowerManager().removeFollower(message.getUserId());
    }

    @Override
    public void groupJoin(Integer groupId, CallbackGroupJoin message) {
        context.getFollowerManager().addFollower(message.getUserId());
    }
}
