package ru.ligastavok.autotests.helpers;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import io.qameta.allure.selenide.AllureSelenide;
import org.aeonbits.owner.ConfigFactory;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import ru.ligastavok.autotests.config.DriverConfig;

import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.logevents.SelenideLogger.addListener;
import static org.openqa.selenium.logging.LogType.BROWSER;

/**
 * Helper class for setting up webdriver
 */
public class DriverHelper {

    private static DriverConfig getDriverConfig() {
        return ConfigFactory.newInstance().create(DriverConfig.class, System.getProperties());
    }

    /**
     * @return webBrowserMobileView config value
     */
    public static String getWebMobile() {
        return getDriverConfig().webBrowserMobileView();
    }

    /**
     * @return <code>true</code> if the mobile view config is set up
     * <code>false</code> otherwise.
     */
    public static boolean isWebMobile() {
        return !getWebMobile().equals("");
    }


    /**
     * Makes remote driver connection url from config
     *
     * @return remote connection url for selenoid or grid
     */
    public static String getWebRemoteDriver() {
        // https://%s:%s@selenoid.autotests.cloud/wd/hub/
        return String.format(getDriverConfig().webRemoteDriverUrl(),
                getDriverConfig().webRemoteDriverUser(),
                getDriverConfig().webRemoteDriverPassword());
    }

    /**
     * @return <code>true</code> if remote driver config is set up
     * <code>false</code> otherwise.
     */
    public static boolean isRemoteWebDriver() {
        return !getDriverConfig().webRemoteDriverUrl().equals("");
    }

    /**
     * @return video storage url from config
     */
    public static String getVideoUrl() {
        return getDriverConfig().videoStorage();
    }

    /**
     * @return <code>true</code> if the video setting config is set up
     * <code>false</code> otherwise.
     */
    public static boolean isVideoOn() {
        return !getVideoUrl().equals("");
    }

    /**
     * @return current webdriver session id
     */
    public static String getSessionId() {
        return ((RemoteWebDriver) getWebDriver()).getSessionId().toString().replace("selenoid", "");
    }

    /**
     * @return current browser console logs
     */
    public static String getConsoleLogs() {
        return String.join("\n", Selenide.getWebDriverLogs(BROWSER));
    }

    /**
     * Configure webdriver object for tests
     */
    public static void configureDriver() {
        addListener("AllureSelenide", new AllureSelenide());
        Configuration.browser = getDriverConfig().webBrowser();
        Configuration.browserVersion = getDriverConfig().webBrowserVersion();
        Configuration.browserSize = getDriverConfig().webBrowserSize();
        Configuration.timeout = 10000;

        ChromeOptions options = new ChromeOptions();
        DesiredCapabilities capabilities = new DesiredCapabilities();
        options.addArguments("--no-default-browser-check");
        options.addArguments("--disable-notifications");

        if (isWebMobile()) { // for chrome only
            Map<String, Object> mobileDevice = new HashMap<>();
            mobileDevice.put("deviceName", getWebMobile());
            options.setExperimentalOption("mobileEmulation", mobileDevice);
            capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        }

        if (isRemoteWebDriver()) {
            capabilities.setCapability("enableVNC", true);
            capabilities.setCapability("enableVideo", true);
            Configuration.remote = getWebRemoteDriver();
        }
        Configuration.browserCapabilities = capabilities;
    }
}
