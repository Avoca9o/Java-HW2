# Java-HW2
Домашнее задание №2 по дисциплине "Конструирование программного обеспечения"

Для тестирования необоходимо в папке проекта создать папку "root",
и уже в ней производить все необходимые махинации.
Программа распознает все зависимости вида "require '<путь к файлу относительно root>'"
к примеру "require 'folder1\file1-1.txt'" заставит добавить этот файл в список
зависимостей файла, в котором эта подстрока будет найдена

Итоговый результат выводится в файл output.txt, который можно найти в папке root.
При возникновении цикла в зависимостях программа завершает работу, не прописывая
ничего в файл output.txt. Вместо этого она выводит сообщение о цикле и сам цикл
в консоль.
При попытке добавления в зависимости несуществующего файла ничего не происходит.
