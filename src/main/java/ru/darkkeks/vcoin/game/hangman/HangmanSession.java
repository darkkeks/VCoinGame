package ru.darkkeks.vcoin.game.hangman;

import com.vk.api.sdk.objects.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.darkkeks.vcoin.game.AppContext;
import ru.darkkeks.vcoin.game.GameSession;
import ru.darkkeks.vcoin.game.Screen;
import ru.darkkeks.vcoin.game.api.Transaction;

public class HangmanSession extends GameSession {

    private static final Logger logger = LoggerFactory.getLogger(HangmanSession.class);

    private Hangman hangman;
    private HangmanState state;
    private Screen<HangmanSession> screen;
    private Screen<HangmanSession> previousScreen;

    public HangmanSession(AppContext context, Hangman hangman, int chatId) {
        super(context, chatId);
        this.hangman = hangman;
        state = hangman.getDao().getState(chatId);
        screen = hangman.getScreen(state);
    }

    public void setScreen(Screen<HangmanSession> screen) {
        if(this.screen != screen) {
            this.previousScreen = this.screen;
            this.screen = screen;
        }
    }

    public Screen<HangmanSession> getPreviousScreen() {
        return previousScreen;
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
        logger.info("Deposit(id = {}, amount = {})", getChatId(), transaction.getAmount());
        state.addCoins(transaction.getAmount());
        hangman.getDao().saveState(getChatId(), state);
        sendMessage(String.format(HangmanMessages.DEPOSIT_SUCCESS, transaction.getAmount() / 1e3));
    }
}
