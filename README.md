## Программирование на Java

[![Build Status](https://travis-ci.org/fediq/mipt-java-2016.svg?branch=master)](https://travis-ci.org/fediq/mipt-java-2016)

Поток 2016го года. [Прогресс потока](https://docs.google.com/spreadsheets/d/1oPpxy26PrXxCu2bypKvkPSyBGBfljd5K6C3si8UhIDQ/edit?usp=sharing).

### Контакты

[Чат курса в Telegram](https://telegram.me/joinchat/BYmS6wmR7ocV3sje1JI17g)

* Лектор - [Федор Лаврентьев](https://github.com/fediq); telegram - [fediq](https://telegram.me/fediq)
* Группы 594 и 595 - [Антон Зверев](https://github.com/malchun)
* Группа 596 - [Даниил Анастасьев](https://github.com/DanAnastasyev)
* Группа 597 - [Александр Кисленко](https://github.com/ignorer); telegram - [ignorer](https://telegram.me/ignorer)

### Домашние задания

1. [Калькулятор](tasks/01-Calculator.md) - дедлайн 14.10.2016 23:59 MSK
2. [Key-value storage](tasks/02-KeyValueStorage.md) - дедлайн 31.10.2016 23:59 MSK
3. [Оптимизация производительности](tasks/03-Performance.md)
  * мягкий дедлайн: 21.11.2016 23:59 MSK (после его наступления можно получить максимум 1 балл)  
  * жесткий дедлайн: 28.11.2016 23:59 MSK
4. [REST API калькулятор](tasks/04-Rest.md) - **черновик!**, дедлайна нет

#### Как оценивается

* Если на момент дедлайна у преподавателя претензий к коду нет, задача считается принятой, за неё даётся 2 балла.
* Если на момент дедлайна тесты проходятся успешно, но у преподавателя есть замечания, задача считается сделанной, за неё дается 1 балл.
* Если на момент дедлайна тесты не проходятся или же pull request вообще не оформлен, задача считается не сделанной, за неё дается 0 баллов.

### Материалы

#### Лекции

1. [Основы](http://www.slideshare.net/FedorLavrentyev/java-01-65838055)
2. [Объекты](http://www.slideshare.net/FedorLavrentyev/java-02-65838195)
3. [Классы](http://www.slideshare.net/FedorLavrentyev/programming-java-lection-03-classes-lavrentyev-fedor)
4. [Обобщения и лямбды](http://www.slideshare.net/FedorLavrentyev/programmning-java-lection-04-generics-and-lambdas-lavrentyev-fedor)
5. [Проектирование](http://www.slideshare.net/FedorLavrentyev/programmning-java-lection-05-software-design-lavrentyev-fedor)
6. [Многопоточность](http://www.slideshare.net/FedorLavrentyev/programming-java-lection-06-multithreading-lavrentyev-fedor)
7. [Бонус - головоломки](http://www.slideshare.net/FedorLavrentyev/programming-java-lection-07-puzzlers-lavrentyev-fedor)
8. [Сборка и компоновка приложения](http://www.slideshare.net/FedorLavrentyev/industrial-programming-java-lection-pack-01-building-an-application-lavrentyev-fedor) (из курса Промышленное программирование)
9. [Распределенные системы](http://www.slideshare.net/FedorLavrentyev/industrial-programming-java-lection-pack-02-distributed-applications-lavrentyev-fedor) (из курса Промышленное программирование)
10. [Реляционные базы данных](http://www.slideshare.net/FedorLavrentyev/industrial-programming-java-lection-pack-03-relational-databases-lavrentyev-fedor) (из курса Промышленное программирование)

#### Рекомендуемая литература

* Кей Хорстманн - [Java. Библиотека профессионала. Том 1. Основы (десятое издание)](https://www.ozon.ru/context/detail/id/137377512/).
* Кэти Сьерра и др - [Head First. Изучаем Java](http://www.ozon.ru/context/detail/id/7821666/).
* Джошуа Блох - [Java. Эффективное программирование](https://www.ozon.ru/context/detail/id/1259354/).
* Brian Goetz et al - [Java Concurrency in Practice](http://www.ozon.ru/context/detail/id/3174887/).
* Эрих Гамма и др - [Приемы объектно-ориентированного проектирования](https://www.ozon.ru/context/detail/id/2457392/).
* Эрик Фриман и др - [Head First. Паттерны проектирования](https://www.ozon.ru/context/detail/id/20216992/).
* Роберт Мартин - [Чистый код](http://www.ozon.ru/context/detail/id/21916535/).
* Eric Redmond et al - [Seven Databases in Seven Weeks](http://shop.oreilly.com/product/9781934356920.do).

##### Документация

* [Oracle Java Code Conventions](http://www.oracle.com/technetwork/java/javase/overview/codeconvtoc-136057.html).
* [Maven: The Definitive Guide](http://shop.oreilly.com/product/9780596517335.do).
* [Spring Framework Reference Documentation](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/).

#### Семинары 596-ой группы
Стараемся придерживаться [google-кодстайла](https://google.github.io/styleguide/javaguide.html).  
Проще всего этого можно добиться, скачав [настройки для кодстайла](https://github.com/DanAnastasyev/mipt-java-2016/blob/master/seminars/src/ru/mipt/java2016/seminars/idea-codestyle.xml). Посмотреть, как их устанавливать, можно [здесь](https://github.com/HPI-Information-Systems/Metanome/wiki/Installing-the-google-styleguide-settings-in-intellij-and-eclipse).  
После этого не забываем использовать кнопку Code/Reformat Code, хотя бы перед коммитом.

Обязательным является прохождение проверки Maven Checkstyle. Она происходит при запуске всех тестов (например, с помощью `mvn test` или по клавише test во вкладке Maven Projects).

1. [Java vs C++; Работа с исключениям и I/O](https://yadi.sk/d/meyw4Nv1wUrzc) ([примеры кода](https://github.com/DanAnastasyev/mipt-java-2016/tree/master/seminars/src/ru/mipt/java2016/seminars/seminar1), [доп семинар](https://github.com/DanAnastasyev/mipt-java-2016/blob/master/seminars/src/ru/mipt/java2016/seminars/Seminar%201.5%20Notes.md))
2. [Паттерны проектирования](https://yadi.sk/d/Clpl7NfhwuYod)  
3. [Строки и обёртки над примитивными типами в Java. Антипаттерны](https://yadi.sk/d/nhdo_dFsx7JnZ)  
4. [Повторное использование кода в Java. Многопоточность в Java](https://yadi.sk/d/nitO2OJuxcMAw)
5. [Средства синхронизации в Java](https://yadi.sk/d/2YAOFlpSxx73h) ([примеры кода](https://github.com/DanAnastasyev/mipt-java-2016/tree/master/seminars/src/ru/mipt/java2016/seminars/seminar5))
6. [Рефлексия и сборка мусора в Java](https://yadi.sk/d/1cpd_baDyHELV) ([примеры кода](https://github.com/DanAnastasyev/mipt-java-2016/tree/master/seminars/src/ru/mipt/java2016/seminars/seminar6), [хорошая статья про ссылки в Java](http://www.kdgregory.com/?page=java.refobj))
7. [Lambdas & Streams](https://yadi.sk/d/uJ4A7PrRyhL2e) ([примеры кода](https://github.com/DanAnastasyev/mipt-java-2016/tree/master/seminars/src/ru/mipt/java2016/seminars/seminar7))

##### Работа с Git

* [Как коммитить](http://chris.beams.io/posts/git-commit/).
* [Merging vs rebasing](https://www.atlassian.com/git/tutorials/merging-vs-rebasing).
* [Pro Git](https://git-scm.com/book/ru/v1).

####  Программа

* Предпосылки к появлению Java
* Примитивы
* java.lang.Object
* Классы, абстрактные классы, интерфейсы, внутренние классы, анонимные классы
* Наследование, переопределение методов
* Модификаторы доступа, инкапсуляция
* Обобщения (Generics)
* Лямбда-выражения
* Пакет java.util.Collections
* Компоновка программного кода
* Многослойная архитектура
* Порождающие шаблоны проектирования
* Структурные шаблоны проектирования
* Поведенческие шаблоны проектирования
* Потоки в JVM
* Критические секции, synchronized
* ~~Java Memory Model~~
* Паттерны организации многопоточного кода
* Проблемы многопоточности
* ~~Сборщик мусора~~
* ~~Стратегии сборки мусора~~
* ~~Слабые ссылки~~
* Пределы нагрузки JVM
* Оптимизация кода
* ~~Профайлинг~~
* ~~Тюнинг параметров JVM~~
* ~~Reflection API~~
* ~~Aspect-Oriented Programming~~
* ~~Abstract Syntax Tree~~
* Фреймворки для компоновки сложных приложений
* Работа с SQL из Java
* HTTP в Java
* Парадигма REST
* Форматы сериализации
* Сравнение Java с другими языками
* Мотивация при выборе языка/фреймворка/сервиса
