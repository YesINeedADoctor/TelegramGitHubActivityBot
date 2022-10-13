# TelegramGitHubActivityBot
A tool for tracking the current activity of github users.
Also a tool for inspiring or motivating developers 😁

You can try it out here - [Bot](https://t.me/GitHubActivity_Bot)  
  Or install it on your local machine.

## Basic installation :octocat:
1) Download repository
2) Change the SpringConfig file - `./src/main/resources/application.properties`

>![image](https://user-images.githubusercontent.com/108088982/195593354-2e9b10ff-ded5-4e8a-8746-7f550cef95a6.png)
  - spring.datasource.url - url to PostgresDB (db name must match the one specified in the class      `./src/main/java/io/project/SpringTelegramGHActivityBot/db/PostgreSqlDBCreator`)
  - spring.datasource.username - the db user who will interact with the database (e.g. root)
  - spring.datasource.password - db user password
  - bot.name - tg bot name
  - bot.token - tg bot token
  
  3) Run `mvn compile`
  4) Run `mvn spring-boot:run`
  5) Enjoy
  
 ## Docker installation 🐳
  1) Download repository
  2) Change the SpringConfig file - `./src/main/resources/application.properties`
  3) Run `mvn compile`
  4) Run `mvn package` (if you are not sure about your db settings run `mvn package -DskipTests`)
  5) Add row to .dockerignore file: `!target/SpringTelegramGHActivityBot-0.0.1-SNAPSHOT.jar`
  6) Run `docker-compose up` (if you are experienced in docker run `docker-compose up -d --build`)
  7) The result should be the following container configuration
  
  >![image](https://user-images.githubusercontent.com/108088982/195602010-b6ee9ae5-1349-43c4-83c9-aeb186a059ab.png)
  
  8) Enjoy
