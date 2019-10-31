package kaehnd;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import java.io.IOException;

public class Account {

    private final String username;
    private final String password;

    public Account(String username, String password) {
        this.password = password;
        this.username = username;
    }

    HtmlPage login(HtmlForm loginForm) throws IOException {
        final HtmlInput button = loginForm.getInputByValue("Submit");
        final HtmlTextInput usernameField = loginForm.getInputByName("user");
        final HtmlPasswordInput passwordField = loginForm.getInputByName("pass");
        usernameField.setValueAttribute(username);
        passwordField.setValueAttribute(password);
        return button.click();
    }

}
