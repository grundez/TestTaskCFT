# Тестовое задание ШИФТ

## 📌 **Описание**
Утилита фильтрует данные в входных файлах на **`integer`**, **`float`** и **`string`**.  
При запуске утилиты входные файлы `*.txt` допускается указывать после описанных параметров.  
Правильность синтаксиса проверяется через ругулярные выражения: путь для флага `-o`, префикс для `-p`, файлы формата `*.txt`, а также фильтрация типа данных: **`integer`**, **`float`** и **`string`** внутри входных файлов.

---
## **Параметры:**

- **`-a`** **Режим добавления в файл.**
 Если флаг указан, результаты будут добавляться в файл. Без флага файл будет перезаписан.  
  

- **`-s`** **Режим краткой сводки.**  
Включает вывод статистики о количестве найденных данных для каждого типа (integer, float, string).
  

- **`-f`** **Режим полной сводки.**  
Включает вывод более подробной статистики, включая минимальные, максимальные значения, сумму и среднее для int и float типа данных. Статистика для строк, помимо их количества, содержит также размер самой
  короткой строки и самой длинной.
  

- **`-o`** **Указание пути для сохранения результатов.**  
Флаг используется для указания директории, в которой будут сохранены результаты. Если не указан, результаты сохраняются в текущей директории.
  

- **`-p`** **Указание префикса имени файла результатов.**  
Этот флаг позволяет добавить префикс к имени файлов результатов.


---
## 🔧 **Требования**
- **Java**: JDK 17+
- **Система сборки**: компиляция через `javac`
- **Сторонние библиотеки**: отсутствуют

---
## Компиляция и запуск:
```sh
javac Main.java
jar cfe util.jar Main Main.class
java -jar util.jar <params> <filename>
```

