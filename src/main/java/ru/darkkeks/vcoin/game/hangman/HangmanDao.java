package ru.darkkeks.vcoin.game.hangman;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.darkkeks.vcoin.game.StateDao;

public class HangmanDao implements StateDao<Integer, HangmanState> {

    private static final Logger logger = LoggerFactory.getLogger(HangmanDao.class);

    @Override
    public HangmanState getState(Integer key) {
        return new HangmanState();
    }

    @Override
    public void saveState(Integer key, HangmanState state) {
        logger.info("Saved state (id={}, counts={}, word={}, letters={}", key, state.getCoins(),
                state.getWord(), state.getGuessedLetters());
    }
}
