package currency.calculator;

public class Conversion {
    int id;
    int userId;
    String convert_from;
    String convert_to;
    String amount;

    public Conversion(int id, int userId, String convert_from, String convert_to, String amount) {
        this.id = id;
        this.userId = userId;
        this.convert_from = convert_from;
        this.convert_to = convert_to;
        this.amount = amount;
    }
}
