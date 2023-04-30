package org.purejava;

import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBus;
import org.junit.jupiter.api.*;
import org.purejava.kwallet.Static;
import org.purejava.kwallet.KDEWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KDEWalletTest implements PropertyChangeListener {
    private final Logger log = LoggerFactory.getLogger(KDEWalletTest.class);
    private Context context;
    private int handle = -1;

    @BeforeEach
    public void beforeEach(TestInfo info) {
        context = new Context(log);
        context.ensureService();
    }

    @AfterEach
    public void afterEach() {
        context.after();
    }

    @Test
    @Order(1)
    @DisplayName("Checking availability of kwallet daemon...")
    public void isEnabled() {
        try {
            var bus = context.connection.getRemoteObject("org.freedesktop.DBus",
                    "/org/freedesktop/DBus", DBus.class);
            assertTrue (Arrays.asList(bus.ListActivatableNames()).contains("org.kde.kwalletd5"));
        } catch (DBusException e) {
            log.error(e.toString(), e.getCause());
        }
    }

    @Test
    @Order(2)
    @DisplayName("Checking availability of wallets...")
    public void wallets() {
        var kwallet = new KDEWallet(context.connection);
        var response = kwallet.send("wallets");
        var walletNames = (List<String>) response[0];
        assertTrue(walletNames.size() > 0);
        assertEquals(walletNames.get(0), Static.DEFAULT_WALLET);
        log.info("Found '{}' as first wallet.", walletNames.get(0));
    }

    @Test
    @Order(3)
    @DisplayName("Testing create folder functionality in locked kdewallet...")
    void testCreateFolder() throws InterruptedException {
        var kwallet = new KDEWallet(context.connection);
        var wallet = Static.DEFAULT_WALLET;
        var wId = 0;
        var appid = "Tester";
        kwallet.getSignalHandler().addPropertyChangeListener(this);
        kwallet.openAsync(wallet, wId, appid, false);
        log.info("You have 10 seconds to enter the password :)");
        Thread.sleep(10000L); // give me 10 seconds to enter the password
        assertTrue(handle > 0);
        if (handle > 0) log.info("Wallet '{}' successfully opened.", Static.DEFAULT_WALLET);
        if (handle > 0) log.info("Received handle: {}.", handle);
        var folder = "Test-Folder";
        var created = kwallet.createFolder(handle, folder, appid);
        assertTrue(created);
        log.info("Folder '{}' successfully created.", folder);
        var removed = kwallet.removeFolder(handle, folder, appid);
        assertTrue(removed);
        log.info("Folder '{}' successfully deleted.", folder);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equals("KWallet.walletAsyncOpened")) handle = (int) event.getNewValue();
    }
}
