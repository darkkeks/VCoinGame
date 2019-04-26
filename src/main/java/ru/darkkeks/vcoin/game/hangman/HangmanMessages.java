package ru.darkkeks.vcoin.game.hangman;

public class HangmanMessages {


    public static final String START = "Начать";
    public static final String PLAY = "🎴 Играть";
    public static final String RULES = "📜 Правила";
    public static final String DEPOSIT = "💰 Пополнить баланс";
    public static final String BALANCE = "💰 Баланс";
    public static final String WITHDRAW = "⏪ Вывести";
    public static final String SETTINGS = "⚙️ Настройки";
    public static final String GO_BACK = "🔙 Назад";
    public static final String TOGGLE_IMAGE = "🎴 Изображение";
    public static final String TOGGLE_GIVE_UP = "Кнопка \"Сдаться\"";
    public static final String FREE_GAME = "Бесплатная игра";
    public static final String ENABLED_FREE_GAME = "Игры теперь бесплатные";
    public static final String DISABLED_FREE_GAME = "Игры теперь платные";
    public static final String DEFINITION = "Определение слова";
    public static final String ENABLED_DEFINITION = "Соси жопу (вкл)";
    public static final String DISABLED_DEFINITION = "Соси жопу (выкл)";
    public static final String ENABLED_GIVE_UP_BUTTON = "❗ Отображение кнопки \"Сдаться\" было включено";
    public static final String DISABLED_GIVE_UP_BUTTON = "❗ Отображение кнопки \"Сдаться\" было выключено";
    public static final String ENABLED_IMAGE = "❗ Отображение статуса игры изображением было включено";
    public static final String DISABLED_IMAGE = "❗ Отображение статуса игры изображением было выключено";
    public static final String GO_BACK_MESSAGE = "✅ Вы успешно вышли из меню настроек";
    public static final String FOLLOW_MESSAGE = "✅ Кстати, Вы можете @vcoingame1 (подписаться на нас), чтобы следить " +
            "за новостями и участвовать в конкурсах!";
    public static final String SUCCESS_WITHDRAW_MESSAGE = "✅ %.3f монет было успешно выведено!";
    public static final String NOT_ENOUGH_WITHDRAW_MESSAGE = "😢 На Вашем балансе недостаточно средств :(";
    public static final String GIVE_UP = "🏳️ Сдаться";
    public static final String LETTER_USED_ALREADY = "🔴 Вы уже использовали эту букву";
    public static final String NOT_ENOUGH_TO_PLAY = "😢 Чтобы начать игру, Вам необходимо %.3f монет";
    public static final String COMMANDS_MESSAGE = "❗ Включите отображение клавиатуры (в правом углу поля для ввода " +
            "сообщения)";
    public static final String WORD_MESSAGE = "❗ Загаданное слово: %s";
    public static final String WORD_DEFINITION = "📋 Определение - «%s»\n\n";
    public static final String WIN_MESSAGE = "🏆 Вы выиграли. Ну ничего, в следующий раз победа будет на моей стороне!";
    public static final String LOSE_MESSAGE = "😢 Вы проиграли...";
    public static final String WITHDRAW_MESSAGE = "⏪ Отправьте количество монет, которые Вы хотите вывести...";
    public static final String SOMETHING_WENT_WRONG = "🔴 Что-то сломалось. Попробуйте немного позже 🔴";
    public static final String AMOUNT_HAS_TO_BE_POSITIVE = "❗ Вы можете выводить только положительное число монет!";
    public static final String DEPOSIT_SUCCESS = "✅ Ваш баланс пополнен на %.3f монет!";
    public static final String DEPOSIT_MESSAGE = "🔗 Для оплаты перейдите по следующей ссылке:\n%s";
    public static final String BALANCE_MESSAGE = "💰 Ваш баланс: %.3f монет";
    public static final String GAME_STATUS_MESSAGE = "📗 Отгаданные буквы: %s\n👿 Неверные попытки: %s";
    public static final String HEALTH_MESSAGE = "\nПопытки: %s";
    public static final String RULES_MESSAGE = "📜 Правила игры виселица\n\nЯ загадываю очень сложное " +
            "слово, а Вы должны отгадать его! Но каким образом?...\n\nПрисылайте мне буквы, о которых хотите получить" +
            " информацию. Если буква есть в слове, то я покажу все позиции, где эта буква встречается. Но если этой " +
            "буквы нет в слове, я добавлю на виселицу одну часть Вашего тела. У вас есть всего 6 попыток, чтобы " +
            "выиграть.\n\nВ настройках можно выбрать режим игры: играть на монеты или бесплатно, давать ли мне Вам " +
            "определение слова или нет.";

    public static final String[] HEALTH = new String[]{
            "❤❤❤❤❤❤",
            "❤❤❤❤❤🖤",
            "❤❤❤❤🖤🖤",
            "❤❤❤🖤🖤🖤",
            "❤❤🖤🖤🖤🖤",
            "❤🖤🖤🖤🖤🖤",
            "🖤🖤🖤🖤🖤🖤",
    };

    public static final String[] IMAGES = new String[]{
            "photo-181113882_456259076",
            "photo-181113882_456259077",
            "photo-181113882_456259078",
            "photo-181113882_456259079",
            "photo-181113882_456259080",
            "photo-181113882_456259081",
            "photo-181113882_456259082",
    };

}