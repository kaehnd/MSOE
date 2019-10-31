package kaehnd;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Temporary class gathering data from MSOE webcard server
 */
public class WebcardCrawler {

    private static final String webcardLoginURL = "https://webcard.msoe.edu/login/ldap.php";
    private static final String histPageURL= "https://webcard.msoe.edu/student/svc_history.php";

    private static HashMap<LocalDate, LinkedList<Double>> dollarsMap;
    private static HashMap<LocalDate, LinkedList<Integer>> swipesMap;

    private static int swipesBalance;
    private static double dollarsBalance;

    public WebcardCrawler() throws Exception{
        this.initialize();
    }

    public void initialize() throws Exception {
        //Initialize and login
        final WebClient webClient = new WebClient();
        final HtmlPage loginPage = webClient.getPage(webcardLoginURL);
        HtmlPage mainPage = logIn(loginPage);

        //Obtain meal dollars
        dollarsBalance = getMealDollars(mainPage);

        //Obtain meals
        swipesBalance = getMeals(mainPage);
        HtmlPage histPage = webClient.getPage(histPageURL);

        //Obtain spending history
        findHistory(histPage);
        webClient.close();
    }

    public int getSwipesBalance() {
        return swipesBalance;
    }

    public double getDollarsBalance() {
        return dollarsBalance;
    }

    public HashMap<LocalDate, LinkedList<Integer>> getSwipesMap() {
        return swipesMap;
    }

    public HashMap<LocalDate, LinkedList<Double>> getDollarsMap() {
        return dollarsMap;
    }

    private static LocalDate stringToDate(String dateString) {
        int month = Integer.parseInt(dateString.substring(0, 2));
        int day = Integer.parseInt(dateString.substring(3, 5));
        int year = Integer.parseInt(dateString.substring(6, 10));
        return LocalDate.of(year, month, day);
    }

    private HtmlPage logIn(HtmlPage loginPage) throws Exception {
        final HtmlForm form = loginPage.getFormByName("frmLogin");
        return (new Account("kaehnd", "**************")).login(form);
    }

    private float getMealDollars(HtmlPage mainPage) {
        Iterator<DomNode> bodyIterator = mainPage.getBody().getChildren().iterator();
        bodyIterator.next();
        final int[] pathToDollars = {4, 2, 1, 4, 4, 12, 2, 8, 4};
        DomNode domNode = iterateChildren(bodyIterator.next(), pathToDollars);
        return Float.parseFloat(domNode.getTextContent().substring(1));
    }

    private int getMeals(HtmlPage mainPage) {
        Iterator<DomNode> bodyIterator = mainPage.getBody().getChildren().iterator();
        bodyIterator.next();
        final int[] pathToMeals = {4, 2, 1, 4, 4, 18, 2, 3, 4};
        DomNode domNode = iterateChildren(bodyIterator.next(), pathToMeals);
        return Integer.parseInt(domNode.getTextContent());
    }

    private void findHistory(HtmlPage htmlPage) throws IOException, InterruptedException {
        HtmlTable histTable = retrieveHistTable(htmlPage);
        Iterator<HtmlTableRow> tableRowsIterator = histTable.getRows().iterator();
        tableRowsIterator.next();

        dollarsMap = new HashMap<>();
        swipesMap = new HashMap<>();
        while (tableRowsIterator.hasNext()) {
            HtmlTableRow row = tableRowsIterator.next();
            if (row.getChildElementCount() == 5) {

                String dateString = row.getCell(0).getTextContent();
                LocalDate date = stringToDate(dateString);

                if (row.getCell(2).getTextContent().contains("Student Meal Dollars")) {
                    double dollars = Double.parseDouble(row.getCell(4).getTextContent().substring(1));
                    if (dollarsMap.get(date) == null) {
                        dollarsMap.put(date, new LinkedList<>());
                    }
                    dollarsMap.get(date).add(dollars);
                }
                if (row.getCell(3).getTextContent().contains("Meal")) {
                    int meals = Integer.parseInt(row.getCell(4).getTextContent());
                    if (swipesMap.get(date) == null) {
                        swipesMap.put(date, new LinkedList<>());
                    }
                    swipesMap.get(date).add(meals);
                }

            }
        }
    }

    private HtmlTable retrieveHistTable(HtmlPage histPage) throws IOException {
        histPage.getElementById("divHist");
        HtmlForm form = (HtmlForm) histPage.getElementById("svcHistForm");
        HtmlSelect typeSelect = (HtmlSelect) histPage.getElementById("mnuPlan");

        HtmlOption desiredOption = typeSelect.getOptionByText("All Plans");
        HtmlOption currentOption = typeSelect.getOptionByText("All Financial Plans");

        typeSelect.setSelectedAttribute(currentOption, false);
        typeSelect.setSelectedAttribute(desiredOption, true);

        HtmlInput buttonInput = (HtmlInput) iterateChildren(form, 6);

        histPage = buttonInput.click();
        histPage.getWebClient().waitForBackgroundJavaScript(1000);

        DomNode div = histPage.getElementById("divHist");
        return (HtmlTable) iterateChildren(div, 10);
    }

    private static DomNode iterateChildren(DomNode domNode, int[] subsetValues) {
        Iterator <DomNode> iterator = domNode.getChildren().iterator();
        if (subsetValues.length != 0) {
            for (int i = 0; i < subsetValues[0] - 1; i++) {
                iterator.next();
            }
            return iterateChildren(iterator.next(), shortenArray(subsetValues));
        } else {
            return domNode;
        }
    }

    private static DomNode iterateChildren(DomNode domNode, int n){
        int[] ints = new int[1];
        ints[0] = n;
        return iterateChildren(domNode, ints);
    }

    private static int[] shortenArray(int[] ints) {
        int [] intsToReturn = new int[ints.length - 1];
        if (intsToReturn.length != 0) {
            for (int i = 0; i < intsToReturn.length; i++) {
                intsToReturn[i] = ints[i + 1];
            }
        }
        return intsToReturn;
    }
}
