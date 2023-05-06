// Пакетът за калкулатора на валутни курсове
package currency.calculator;

// Класът Conversion, който съдържа информация за валутните преводи
public class Conversion {
    // Полета за идентификатор, потребителски идентификатор, валута от която конвертираме, валута към която конвертираме и сумата
    int id;
    int userId;
    String convert_from;
    String convert_to;
    String amount;

    // Конструктор на класа Conversion, който приема всички полета като параметри
    public Conversion(int id, int userId, String convert_from, String convert_to, String amount) {
        // Задаване на стойностите на полетата с подадените параметри
        this.id = id;
        this.userId = userId;
        this.convert_from = convert_from;
        this.convert_to = convert_to;
        this.amount = amount;
    }
}
