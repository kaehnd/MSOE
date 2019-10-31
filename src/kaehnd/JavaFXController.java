package kaehnd;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import extfx.scene.chart.DateAxis;
import javafx.util.StringConverter;

public class JavaFXController {

   @FXML
   private Pane dollarChartPane;

   @FXML
   private Pane mealChartPane;

   @FXML
   private Label calcOutput;


    @FXML
    public void build() {
        try {
            MealPlanCalculator calculator = new MealPlanCalculator(new WebcardCrawler(),
                    LocalDate.of(2019, 9, 15), LocalDate.now(),
                    LocalDate.of(2019, 11, 23));

            ObservableList<XYChart.Series<Date, Number>> dollarsSeries = FXCollections.observableArrayList();
            ObservableList<XYChart.Data<Date, Number>> dollarSeriesData = FXCollections.observableArrayList();



            ObservableList<XYChart.Series<Date, Number>> mealSeries = FXCollections.observableArrayList();
            ObservableList<XYChart.Data<Date, Number>> mealSeriesData = FXCollections.observableArrayList();

            calculator.createGraph(new GraphCreateFunction() {
                @Override
                public void setToGraph(LocalDate date, double dollars, int meals) {
                    dollarSeriesData.add(new XYChart.Data<>(new GregorianCalendar(date.getYear(), date.getMonthValue()-1, date.getDayOfMonth()).getTime(),dollars));
                    mealSeriesData.add(new XYChart.Data<>(new GregorianCalendar(date.getYear(), date.getMonthValue()-1, date.getDayOfMonth()).getTime(),meals));
                }
            });


            StringConverter<Number> dollarsConverter = new StringConverter<Number>() {
                @Override
                public String toString(Number object) {
                    return String.format( "$%.2f", object.doubleValue());
                }
                @Override
                public Number fromString(String string) {
                    return Double.parseDouble(string.substring(1));
                }
            };

            dollarsSeries.add(new XYChart.Series<>(dollarSeriesData));
            NumberAxis dollarNumberAxis = new NumberAxis();

            dollarNumberAxis.setTickLabelFormatter(dollarsConverter);
            DateAxis dollarsDateAxis = new DateAxis();

            LineChart<Date, Number> dollarsChart = new LineChart<>(dollarsDateAxis, dollarNumberAxis, dollarsSeries);

            dollarsChart.setPrefWidth(dollarChartPane.getPrefWidth());
            dollarsChart.setPrefHeight(dollarChartPane.getPrefHeight());
            dollarChartPane.getChildren().add(dollarsChart);





            StringConverter<Number> mealConverter = new StringConverter<Number>() {
                @Override
                public String toString(Number object) {
                    return String.format( "%d", object.intValue());
                }
                @Override
                public Number fromString(String string) {
                    return Double.parseDouble(string.substring(1));
                }
            };


            mealSeries.add(new XYChart.Series<>(mealSeriesData));
            NumberAxis mealNumberAxis = new NumberAxis();
            mealNumberAxis.setTickUnit(1);
            mealNumberAxis.setAutoRanging(false);
            mealNumberAxis.setLowerBound(0);
            mealNumberAxis.setUpperBound(3);
            mealNumberAxis.setTickLabelFormatter(mealConverter);


            Date startDate = (new GregorianCalendar(calculator.getStartDate().getYear(),
                    calculator.getStartDate().getMonthValue()-1,
                    calculator.getStartDate().getDayOfMonth()).getTime());
            Date endDate = (new GregorianCalendar(calculator.getEndDate().getYear(),
                    calculator.getEndDate().getMonthValue()-1,
                    calculator.getEndDate().getDayOfMonth()).getTime());

            DateAxis mealsDateAxis = new DateAxis();




            LineChart<Date, Number> mealChart = new LineChart<>(mealsDateAxis, mealNumberAxis, mealSeries);

            mealChart.setPrefWidth(mealChartPane.getPrefWidth());
            mealChart.setPrefHeight(mealChartPane.getPrefHeight());
            mealChartPane.getChildren().add(mealChart);
            System.out.println("Chart Successful");






            System.out.println("Dollars: $" +calculator.getDollarsBalance());
            System.out.println("Meals: " + calculator.getMealBalance());

            String stringtoSet = String.format("Dollars: $%.2f\nMeals: %d\nDays Past: %d\n" +
                            "Dollars per Day Past: $%.2f\nMeals per Day Past: %.2f\nDays Left: %d\n" + "" +
                            "Dollars per Day Left: $%.2f\nMeals per Day Left: %.2f\n\n%s\n%s",
                    calculator.getDollarsBalance(), calculator.getMealBalance(), calculator.getDaysSoFar(),
                    calculator.getDollarsPerDay(), calculator.getMealsPerDay(), calculator.getDaysLeft(),
                    calculator.getDollarsPerDayLeft(), calculator.getMealsPerDayLeft(),
                    calculator.getDollarsAdvice(), calculator.getMealsAdvice());
            System.out.println(stringtoSet);
            calcOutput.setText(stringtoSet);



            System.out.println("Done");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
