package kaehnd;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Performs Analytics on information obtained from MSOE Webcard server
 */
public class MealPlanCalculator {

    private WebcardCrawler crawler;
    private LocalDate startDate;
    private LocalDate endDate;

    private LocalDate endQuarter;

    private int daysSoFar;
    private int daysLeft;

    private double dollarsSpent;
    private int mealsSpent;





    public MealPlanCalculator(WebcardCrawler crawler, LocalDate startDate, LocalDate endDate, LocalDate endQuarter) {
        this.crawler = crawler;
        this.startDate = startDate;
        this.endDate = endDate;
        this.endQuarter = endQuarter;

        this.daysSoFar = startDate.datesUntil(endDate)
                .mapToInt( e -> 1)
                .sum();
        this.daysLeft = endDate.datesUntil(endQuarter)
                .mapToInt( e -> 1)
                .sum();
    }

    public void createGraph(GraphCreateFunction graphCreator) {
        dollarsSpent = 0;
        mealsSpent = 0;

        HashMap<LocalDate, LinkedList<Double>> dollarsMap = crawler.getDollarsMap();
        HashMap<LocalDate, LinkedList<Integer>> swipesMap = crawler.getSwipesMap();


        for (LocalDate date = startDate; date.compareTo(endDate) <= 0; date = date.plusDays(1)) {

            LinkedList<Double> dollarsList = dollarsMap.get(date);
            double totalDollars = 0;
            if (dollarsList != null) {
                for (double amout: dollarsList) {
                    totalDollars += amout;
                }
            }




            LinkedList<Integer> swipesList = swipesMap.get(date);
            int totalSwipes = 0;
            if (swipesList != null) {
                for (double amout: swipesList) {
                    totalSwipes += amout;
                }
            }
            graphCreator.setToGraph(date, totalDollars, totalSwipes);
            dollarsSpent += totalDollars;
            mealsSpent += totalSwipes;
        }
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public int getMealBalance() {
        return crawler.getSwipesBalance();
    }

    public double getDollarsBalance() {
        return crawler.getDollarsBalance();
    }

    public int getDaysLeft() {
        return daysLeft;
    }

    public int getDaysSoFar() {
        return daysSoFar;
    }

    public double getDollarsPerDay() {
        return dollarsSpent / daysSoFar;
    }

    public double getMealsPerDay() {
        return mealsSpent / (double) daysSoFar;
    }

    public double getDollarsPerDayLeft() {
        return getDollarsBalance() / daysLeft;
    }

    public double getMealsPerDayLeft() {
        return getMealBalance() / (double) daysLeft;
    }

    public String getDollarsAdvice() {
        double dailyDifference = getDollarsPerDayLeft() - getDollarsPerDay();
        double lumpDifference = Math.abs(dailyDifference);
        int days = 1;
        while(lumpDifference < 1) {
            lumpDifference += Math.abs(dailyDifference);
            days++;
        }
        String lessMore = dailyDifference < 0 ? "less" : "more";
        String dayDays = days > 1 ? "days" : "day";

        return String.format("You should use $%.2f %s every %s", lumpDifference, lessMore, dayDays);

    }

    public String getMealsAdvice() {
        double dailyDifference = getMealsPerDayLeft() - getMealsPerDay();
        double lumpDifference = Math.abs(dailyDifference);
        int days = 1;
        while(lumpDifference < 1) {
            lumpDifference += Math.abs(dailyDifference);
            days++;
        }
        String lessMore = dailyDifference < 0 ? "fewer" : "more";

        return "You should use one " + lessMore + " meal every " + days + " days.";
    }




}
