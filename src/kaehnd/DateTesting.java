package kaehnd;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;

public class DateTesting {

    public static void main(String[] args) {
        HashMap<LocalDate, LinkedList<Double>> mealDollarsMap = new HashMap<>();
        if (mealDollarsMap.get(LocalDate.now()) == null) {
            mealDollarsMap.put(LocalDate.now(), new LinkedList<>());
        }
        mealDollarsMap.get(LocalDate.now()).add(4.5);
        if (mealDollarsMap.get(LocalDate.now()) == null) {
            mealDollarsMap.put(LocalDate.now(), new LinkedList<>());
        }
        mealDollarsMap.get(LocalDate.now()).add(5.5);
        mealDollarsMap.get(LocalDate.now()).forEach(e -> System.out.println(e));
    }
}