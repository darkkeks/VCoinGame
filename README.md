# VCoinGame - Виселица

Игровой бот "виселица" для каталога игр VkCoin

[vk.com/vcoingame1](https://vk.com/vcoingame1)

## Запуск
   
1. Склонировать себе репозиторий
    ```shell script
    git clone https://github.com/DarkKeks/VCoinGame.git
    cd VCoinGame
    ```
   
1. Собрать бота
    ```shell script
    ./gradlew build
    ```
 
1. Создать таблицы из файла `schema.sql`
   
1. Установить значения переменных окружения

    | Переменная        | Описание |
    | ----------------- | -------- |
    | GROUP_ID          | id группы бота |
    | GROUP_TOKEN       | токен группы с доступом к long polling |
    | TOP_GROUP_ID      | id пользователя для постинга ежедневного топа игроков |
    | TOP_GROUP_TOKEN   | токен пользователя с доступом к стене группы с топом |
    | DATABASE_URL      | uri базы данных вида `jdbc:postgresql://host/database |
    | DATABASE_USERNAME | имя пользователя базы данных |
    | DATABASE_PASSWORD | пароль пользователя базы данных |
    | VCOIN_ID          | id пользователя, используемого для хранения коинов |
    | VCOIN_KEY         | ключ для merchant апи vkcoin |
    | VCOIN_PAYLOAD     | payload для транзакций |
    
1. Запустить
    ```shell script
    java -jar build/libs/game-1.0-SNAPSHOT-all.jar
    ```
    
