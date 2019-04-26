package ru.darkkeks.vcoin.game.hangman;

import ru.darkkeks.vcoin.game.AppContext;
import ru.darkkeks.vcoin.game.Game;
import ru.darkkeks.vcoin.game.Screen;
import ru.darkkeks.vcoin.game.api.Transaction;
import ru.darkkeks.vcoin.game.hangman.screen.GameScreen;
import ru.darkkeks.vcoin.game.hangman.screen.MainScreen;
import ru.darkkeks.vcoin.game.hangman.screen.SettingsScreen;
import ru.darkkeks.vcoin.game.hangman.screen.WithdrawScreen;

import java.util.function.Consumer;

public class Hangman extends Game<HangmanSession> {

    private static final long DEFINITION_BET = 300_000;
    public static final long BASE_BET = 1_000_000;

    private AppContext context;
    private HangmanDao hangmanDao;

    private MainScreen mainScreen;
    private GameScreen gameScreen;
    private WithdrawScreen withdrawScreen;
    private SettingsScreen settingsScreen;

    private WordGenerator generator;
    private WordDescGenerator descGenerator;

    public Hangman(AppContext context) {
        this.context = context;
        this.hangmanDao = new HangmanDao(context.getDataSource());
        this.generator = new WordGenerator();
        this.descGenerator = new WordDescGenerator();

        mainScreen = new MainScreen(this);
        gameScreen = new GameScreen(this);
        withdrawScreen = new WithdrawScreen(this);
        settingsScreen = new SettingsScreen(this);
    }

    private long getBet(HangmanState state) {
        if(state.isFreeGame()) return 0;
        if(state.isDefinition()) return DEFINITION_BET;
        return BASE_BET;
    }

    public void startGame(HangmanSession session) {
        long bet = getBet(session.getState());

        if (session.getState().getCoins() >= bet) {
            session.getState().addCoins(-bet);

            String word = session.getState().isDefinition() ? descGenerator.getWord() : generator.getWord();

            session.getState().setBet(bet);
            session.getState().setWord(word);
            session.getState().setGuessedLetters("");

            gameScreen.sendGameMessage(session);
        } else {
            session.sendMessage(String.format(HangmanMessages.NOT_ENOUGH_TO_PLAY, bet / 1e3),
                    session.getScreen().getKeyboard(session));
        }
    }

    public void handleDeposit(HangmanSession session) {
        String message = String.format(HangmanMessages.DEPOSIT_MESSAGE,
                context.getVCoinApi().getPaymentLink(BASE_BET));
        if(!context.getFollowerManager().isFollower(session.getChatId())) {
            message += "\n\n" + HangmanMessages.FOLLOW_MESSAGE;
        }
        session.sendMessage(message, session.getScreen().getKeyboard(session));
    }

    public void handleWithdraw(HangmanSession session) {
        String message = HangmanMessages.WITHDRAW_MESSAGE;
        if(!context.getFollowerManager().isFollower(session.getChatId())) {
            message += "\n\n" + HangmanMessages.FOLLOW_MESSAGE;
        }
        session.sendMessage(message, session.getScreen().getKeyboard(session));
        session.setScreen(withdrawScreen);
    }

    public void handleBalance(HangmanSession session) {
        String message = String.format(HangmanMessages.BALANCE_MESSAGE,
                session.getState().getCoins() / 1000.0);
        if(!context.getFollowerManager().isFollower(session.getChatId())) {
            message += "\n\n" + HangmanMessages.FOLLOW_MESSAGE;
        }
        session.sendMessage(message, session.getScreen().getKeyboard(session));
    }

    @Override
    public Consumer<Transaction> getTransferConsumer() {
        return transaction -> {
            if(transaction.getTo() == context.getVCoinApi().getUserId()) {
                getSession(transaction.getFrom()).acceptPayment(transaction);
            }
        };
    }

    public Screen<HangmanSession> getScreen(HangmanState state) {
        return state.inGame() ? gameScreen : mainScreen;
    }

    @Override
    protected HangmanSession createSession(int chatId) {
        return new HangmanSession(context, this, chatId);
    }

    public MainScreen getMainScreen() {
        return mainScreen;
    }

    public SettingsScreen getSettingsScreen() {
        return settingsScreen;
    }

    public HangmanDao getDao() {
        return hangmanDao;
    }

    public AppContext getContext() {
        return context;
    }

    public WordDescGenerator getDescGenerator() {
        return descGenerator;
    }
}
