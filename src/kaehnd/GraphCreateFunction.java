package kaehnd;

import java.time.LocalDate;

@FunctionalInterface
public interface GraphCreateFunction {
    void setToGraph(LocalDate date, double dollars, int meals);
}
