// src/Main.java

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Main {

    // Регулярные выражения для проверки синтаксиса
    private static final Pattern INTEGER_PATTERN = Pattern.compile("[-+]?\\d+"); // Целое
    private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?\\d*\\.\\d+([eE][-+]?\\d+)?"); //
    private static final Pattern TXT_FILE_PATTERN = Pattern.compile(".*\\.txt$"); // Файл текстовый
    private static final Pattern VALID_PATH = Pattern.compile("^[^/\\\\:*?\"<>|\\r\\n]+(?:[\\\\/][^/\\\\:*?\"<>|\\r\\n]+)*\\/?$");
    private static final Pattern VALID_PREFIX = Pattern.compile("^[a-zA-Z0-9_-]+$");
    private static final Pattern POSITIVE_INTEGER = Pattern.compile("[1-9]\\d*");

    // Флаги и конфигурация их параметров
    public static boolean sFlag = false, fFlag = false, aFlag = false, pFlag = false, oFlag = false, rFlag = false;
    public static String prefix = "", path = "";
    public static int repeatCount = 1;

    // Метод считывавния информации с файлов
    public static void readFile(String fileName, List<String> data){
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
            String line;
            while((line = br.readLine()) != null){
                data.add(line);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // Метод записи результататов в файл
    public static void writeFile(String filename, List<String> data) {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filename, aFlag))){
            for (String line : data) {
                bw.write(line);
                bw.write("\n"); // Снос строки - разделитель
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // Метод реализации утилиты
    public static void util(String[] args){

        // Описание флагов допускается только перед входным файлом/файлами
        if(!args[args.length-1].endsWith(".txt")){
            System.out.println("Error: excepted *.txt file, bot got: " + args[args.length-1] +"\nUsage: java -jar util.jar <flags> <filename>");
            System.exit(1);
        }

        // Сканнер флагов и их параметров
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-s":
                    if(!fFlag){
                        sFlag = true;
                    }
                    else{
                        System.out.println("Flag -s was corrupt by -f");
                    }
                    break;
                case "-f":
                    fFlag = true;
                    if(sFlag){
                        sFlag = false;
                        System.out.println("Flag -s was corrupt by -f");
                    }
                    break;
                case "-a": aFlag = true; break;
                case "-p":
                    pFlag = true;
                    if(!VALID_PREFIX.matcher(args[i + 1]).matches()){
                        System.out.println("Error: invalid prefix syntax, got: " + args[i + 1] + "\nPrefix value: \"\"");
                        prefix = "";
                    }
                    else {
                        prefix = args[i + 1];
                        i++;
                    }
                    break;
                case "-o":
                    oFlag = true;
                    if(TXT_FILE_PATTERN.matcher(args[i + 1]).matches()){
                        System.out.println("Error: invalid output path, got file: " + args[i + 1]);
                        System.exit(1);
                    }
                    else if(!VALID_PATH.matcher(args[i + 1]).matches()){
                        System.out.println("Error: invalid output path, got: " + args[i + 1]);
                        System.exit(1);
                    }
                    else{
                        path = args[i + 1];
                        i++;
                    }
                    break;
                case "-r":
                    if(!POSITIVE_INTEGER.matcher(args[i + 1]).matches()){
                        System.out.println("Error: invalid repeat param, excepted positive integer number, got: " + args[i + 1]);
                        System.exit(1);
                    }
                    else{
                        rFlag = true;
                        repeatCount = Integer.parseInt(args[i + 1]);
                    }
                    break;
                default: break;
            }
        }

        // Собираем все файлы
        List<String> files = new ArrayList<>();
        for (String arg : args) {
            // Проверяем, что аргумент - файл формата *.txt и уникален
            if (TXT_FILE_PATTERN.matcher(arg).matches() && !files.contains(arg)) {
                // Отработка флага -r
                for(int i = 0; i <= repeatCount - 1; i++){
                    files.add(arg);
                }
            }
            // Если файл уже есть - выведем сообщение, но не проверим
            else if(files.contains(arg)){
                System.out.println("File already in collection: " + arg);
            }
            // Если файл не соответствует формату *.txt и не является валидным префиксом
            else if (!VALID_PREFIX.matcher(arg).matches()) {
                System.out.println("Skip invalid TXT file: " + arg);
            }
        }

        if(files.isEmpty()){
            System.out.println("No files found");
            System.exit(1);
        }

        // Проверяем, что файлы имеют расширение *.txt
        List<String> data = new ArrayList<>();
        for (String file : files) {
            /* !!! Бесполезная проверка после добавления регулярных выражений !!!
            if (!file.endsWith(".txt")) {
                System.out.println("Error: expected .txt file, but got: " + file);
                System.exit(1);
            }
            else{
                readFile(file, data);
            }
            */

            readFile(file, data);
        }

        // Создаю контейнеры под разные типы
        List<String> intList = new ArrayList<>();
        List<String> floatList = new ArrayList<>();
        List<String> strList = new ArrayList<>();

        // Фильтруем дату
        for(String line : data){
            String type = checkDataType(line);
            switch(type){
                case "int": intList.add(line); break;
                case "float": floatList.add(line); break;
                case "str": strList.add(line); break;
                default: break;
            }
        }

        // Проверка что контейнеры не пустые, иначе не записываем в файл
        if (!intList.isEmpty()) {
            writeFile(path + prefix + "integers.txt", intList);
        }
        if (!floatList.isEmpty()) {
            writeFile(path + prefix + "floats.txt", floatList);
        }
        if (!strList.isEmpty()) {
            writeFile(path + prefix + "strings.txt", strList);
        }

        // Краткая статистика
        if(sFlag){
            System.out.println("\n=Short stat=");
            System.out.println("Integers added count: " + intList.size());
            System.out.println("Floats added count: " + floatList.size());
            System.out.println("Strings added count: " + strList.size());
        }

        // Полная статистика
        if(fFlag){
            long minInt, maxInt, sumInt; // Параметры статисики int данных
            float minFloat, maxFloat, sumFloat; // Параметры статисики float данных
            long minStr, maxStr; // Параметры статисики str данных
            String minStringType, maxStringType;

            /* Инициализация, для сортировки min/max сначала беру первый элемент контейнеров
            * и сравниваю его с отсальными элементами, вместо установки 1e9/-1e9 для min/max, соотвественно
            *  */
            if(!intList.isEmpty()){
                minInt = Long.parseLong(intList.getFirst());
                maxInt = Long.parseLong(intList.getFirst());
                // В сумму положил первый элемент, тк циклы со второго
                sumInt = Long.parseLong(intList.getFirst());
            }
            else{
                minInt = 0;
                maxInt = 0;
                sumInt = 0;
            }

            if(!floatList.isEmpty()){
                minFloat = Float.parseFloat(floatList.getFirst());
                maxFloat = Float.parseFloat(floatList.getFirst());
                // В сумму положил первый элемент, тк циклы со второго
                sumFloat = Float.parseFloat(floatList.getFirst());
            }
            else{
                minFloat = 0;
                maxFloat = 0;
                sumFloat = 0;
            }

            if(!strList.isEmpty()){
                // Аналогично, начинаем с первой строки в случае непустого контейнера
                minStr = strList.getFirst().length();
                maxStr = strList.getFirst().length();
                maxStringType = strList.getFirst();
                minStringType = strList.getFirst();
            }
            else{
                minStr = 0;
                maxStr = 0;
                maxStringType = "";
                minStringType = "";
            }

            // Поиск мин/макс и суммы
            for(int i = 1; i < intList.size(); i++){
                long numInt = Long.parseLong(intList.get(i));
                sumInt += numInt;
                if(numInt < minInt) minInt = numInt;
                if(numInt > maxInt) maxInt = numInt;
            }

            for(int i = 1; i < floatList.size(); i++){
                float numFloat = Float.parseFloat(floatList.get(i));
                sumFloat += numFloat;
                if(numFloat < minFloat) minFloat = numFloat;
                if(numFloat > maxFloat) maxFloat = numFloat;
            }

            for(int i = 1; i < strList.size(); i++){
                int strSize = strList.get(i).length();
                if(strSize < minStr) {
                    minStr = strSize;
                    minStringType = strList.get(i);
                }
                if(strSize > maxStr) {
                    maxStr = strSize;
                    maxStringType = strList.get(i);
                }
            }

            // Поиск средних значений для int и float
            float intAverage = 0;
            float floatAverage = 0;

            // Предотвращаем деление на 0, если список пуст
            if(!intList.isEmpty()){
                intAverage = (float) sumInt / intList.size();
            }
            else{
                intAverage = 0;
            }

            // Предотвращаем деление на 0, если список пуст
            if(!floatList.isEmpty()){
                floatAverage = sumFloat / floatList.size();
            }
            else{
                floatAverage = 0;
            }

            System.out.println("\n=Full stat=");
            System.out.println("-------------\nIntegers added count: " + intList.size() + "\nMin: " + minInt +"\nMax: " + maxInt + "\nSum: " + sumInt + "\nAvg: " + intAverage);
            System.out.println("-------------\nFloats added count: " + floatList.size() + "\nMin: " + minFloat +"\nMax: " + maxFloat + "\nSum: " + sumFloat + "\nAvg: " + floatAverage);
            System.out.println("-------------\nStrings added count: " + strList.size() + "\nMin string: " + minStringType + ", size: " + minStr + "\nMax string: " + maxStringType + ", size: " +  maxStr + "\n-------------");
        }
    }

    // Метод проверки типа даты
    public static String checkDataType(String str){
        if (INTEGER_PATTERN.matcher(str).matches()){
            return "int";
        }
        if (FLOAT_PATTERN.matcher(str).matches()){
            return "float";
        }
        return "str";
    }

    public static void main(String[] args) {
        // Запускаем утилиту
        util(args);
    }
}