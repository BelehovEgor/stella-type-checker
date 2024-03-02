# stella-type-checker

Сборка проекта

```bash
./gradlew build
```

Запуск

```bash
java -jar build/libs/stella-type-checker.jar
```

Запуск тестов

```bash
./gradlew test
```

Программа при запуске ожидает программу на языке
Stella на stdin с EOF (Ctrl+D) в конце, после чего производит
проверку типов.