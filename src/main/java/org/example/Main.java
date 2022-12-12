package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    static final String GREETING_1_MESSAGE = "Введите математическое выражение (пример: 5 + 2, 10 / 5, VII + III) и нажмите Enter";
    static final String GREETING_2_MESSAGE = "Для выхода из программы введите quit";
    static final String INCORRECT_MATH_OPERATION_MESSAGE = "Введите корректное математическое выражение";
    static final String NULL_STRING_EXCEPTION_MESSAGE = "Строка не может быть пустой";
    static final String ROMAN_NEGATIVE_EXCEPTION_MESSAGE = "В римской системе нет отрицательных чисел";
    static final String PROGRAM_EXIT_MESSAGE = "Программа завершена";

    enum RomanNumeral {
        I(1), IV(4), V(5), IX(9), X(10),
        XL(40), L(50), XC(90), C(100),
        CD(400), D(500), CM(900), M(1000);

        private final int value;

        RomanNumeral(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static List<RomanNumeral> getReverseSortedValues() {
            return Arrays.stream(values())
                    .sorted(Comparator.comparing((RomanNumeral e) -> e.value).reversed())
                    .collect(Collectors.toList());
        }
    }

    // Разбивает входящюю строку на список (по условию X + X etc)
    static List<String> split(String input) throws NullPointerException {
        if (input == null)
            throw new NullPointerException(NULL_STRING_EXCEPTION_MESSAGE);

        List<String> result = new ArrayList<>();

        String[] operators = new String[]{"+", "-", "*", "/"};
        int index = 0;
        while (index < input.length()) {
            int minimum = input.length();
            for (String operator : operators) {
                int i = input.indexOf(operator, index);
                if (i > -1)
                    minimum = Math.min(minimum, i);
            }

            if (minimum < input.length()) {
                result.add(input.substring(index, minimum).trim());
                result.add("" + input.charAt(minimum));
                index = minimum + 1;
            } else {
                result.add(input.substring(index).trim());
                break;
            }
        }
        return result;
    }

    // Проверяем арабские числа от 1 до 10
    static boolean arabicMath(String input) {
        try {
            int value = Integer.parseInt(input.trim());
            if (value >= 1 && value <= 10) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    // Проверяем римские числа от 1 до 10
    static boolean romanMath(String input) {
        String[] roman = new String[]{"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        for (String s : roman)
            if (s.equals(input.trim()))
                return true;
        return false;
    }

    // Перевод римского числа в арабское
    static int romanToArabic(String input) {
        String romanNumeral = input.toUpperCase();
        int result = 0;

        List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

        int i = 0;

        while ((romanNumeral.length() > 0) && (i < romanNumerals.size())) {
            RomanNumeral symbol = romanNumerals.get(i);
            if (romanNumeral.startsWith(symbol.name())) {
                result += symbol.getValue();
                romanNumeral = romanNumeral.substring(symbol.name().length());
            } else {
                i++;
            }
        }

        if (romanNumeral.length() > 0) {
            throw new IllegalArgumentException(input + " cannot be converted to a Roman Numeral");
        }

        return result;
    }

    // Перевод арабское число в римское
    public static String arabicToRoman(int number) {
        if ((number <= 0) || (number > 4000)) {
            throw new IllegalArgumentException(number + " is not in range (0,4000]");
        }
        List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

        int i = 0;
        StringBuilder sb = new StringBuilder();

        while ((number > 0) && (i < romanNumerals.size())) {
            RomanNumeral currentSymbol = romanNumerals.get(i);
            if (currentSymbol.getValue() <= number) {
                sb.append(currentSymbol.name());
                number -= currentSymbol.getValue();
            } else {
                i++;
            }
        }
        return sb.toString();
    }

    // вычисление
    static int calculation(int first, int second, String operator) {
        switch (operator) {
            case "+":
                return first + second;
            case "-":
                return first - second;
            case "*":
                return first * second;
            case "/":
                return first / second;
            default:
                return 0;
        }
    }

    // арабские числа и расчет
    static int arabicCalc(List<String> inputArray) {
        int first = Integer.parseInt(inputArray.get(0));
        String operator = inputArray.get(1);
        int second = Integer.parseInt(inputArray.get(2));
        return calculation(first, second, operator);
    }

    // римские числа, перевод в арабские и расчет, а так же исключение если входящие числа не соответствуют требованиям
    static String romanCalc(List<String> inputArray) throws Exception {
        int first = romanToArabic(inputArray.get(0));
        String operator = inputArray.get(1);
        int second = romanToArabic(inputArray.get(2));

        if (first < 1 || first > 10 || second < 1 || second > 10)
            throw new Exception(INCORRECT_MATH_OPERATION_MESSAGE);

        int result = calculation(first, second, operator);

        if (result < 1) {
            throw new Exception(ROMAN_NEGATIVE_EXCEPTION_MESSAGE);
        }
        return arabicToRoman(result);
    }

    // Калькулятор
    public static String calc(String input) throws Exception {

        List<String> inputArray = split(input);
        //-- Состоит ли выражение из 3 частей, согласно условиям
        if (inputArray.size() == 3) {
            //-- Проверим на соответсвие арабским или римским цифрам
            if (arabicMath(inputArray.get(0))) { //-- первый операнд
                if (arabicMath(inputArray.get(2))) { //-- второй операнд
                    //-- арабский калькулятор
                    return arabicCalc(inputArray) + "";
                }
            } else {
                if (romanMath(inputArray.get(2))) { //-- второй операнд римский?
                    //-- римский калькулятор
                    return romanCalc(inputArray) + "";
                }
            }
        }
        throw new Exception(INCORRECT_MATH_OPERATION_MESSAGE);
    }

    public static void main(String[] args) throws Exception {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String str, result;

        System.out.println(GREETING_1_MESSAGE);
        System.out.println(GREETING_2_MESSAGE);

        do {
            str = reader.readLine();
            if (str.length() >= 3) { //-- минимальная длина строки "1+1" = 3 символа
                result = calc(str);
                System.out.println(result);
            } else {
                throw new Exception(INCORRECT_MATH_OPERATION_MESSAGE);
            }
        }
        while (!str.equals("quit"));
        System.out.println(PROGRAM_EXIT_MESSAGE);
    }
}