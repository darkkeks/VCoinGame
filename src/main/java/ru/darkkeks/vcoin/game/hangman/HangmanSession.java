package ru.darkkeks.vcoin.game.hangman;

import com.vk.api.sdk.objects.messages.Message;
import ru.darkkeks.vcoin.game.AppContext;
import ru.darkkeks.vcoin.game.GameSession;
import ru.darkkeks.vcoin.game.Screen;
import ru.darkkeks.vcoin.game.api.Transaction;

public class HangmanSession extends GameSession {

    private Hangman hangman;
    private HangmanState state;
    private Screen<HangmanSession> screen;

    public HangmanSession(AppContext context, Hangman hangman, int chatId) {
        super(context, chatId);
        this.hangman = hangman;
        state = hangman.getDao().getState(chatId);
        screen = hangman.getScreen(state);
    }

    public void setScreen(Screen<HangmanSession> screen) {
        this.screen = screen;
    }

    public Screen<HangmanSession> getScreen() {
        return screen;
    }

    public HangmanState getState() {
        return state;
    }

    @Override
    public void acceptMessage(Message message) {
        screen.acceptMessage(message, this);
    }

    @Override
    public void acceptPayment(Transaction transaction) {
        state.addCoins(transaction.getAmount());
        hangman.getDao().saveState(getChatId(), state);
    }
}
