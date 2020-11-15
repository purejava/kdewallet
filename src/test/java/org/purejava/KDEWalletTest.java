package org.purejava;

import org.freedesktop.DBus;
import org.freedesktop.dbus.exceptions.DBusException;
import org.junit.jupiter.api.*;
import org.kde.Static;
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
            DBus bus = context.connection.getRemoteObject("org.freedesktop.DBus",
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
        KDEWallet kwallet = new KDEWallet(context.connection);
        Object[] response = kwallet.send("wallets");
        List<String> walletNames = (List<String>) response[0];
        assertTrue(walletNames.size() > 0);
        assertEquals(walletNames.get(0), Static.DEFAULT_WALLET);
        log.info("Found " + "'" + walletNames.get(0) + "' as first wallet.");
    }

    @Test
    @Order(3)
    @DisplayName("Testing create folder functionality in locked kdewallet...")
    void testCreateFolder() throws InterruptedException {
        KDEWallet kwallet = new KDEWallet(context.connection);
        String wallet = Static.DEFAULT_WALLET;
        int wId = 0;
        String appid = "Tester";
        kwallet.getSignalHandler().addPropertyChangeListener(this);
        kwallet.openAsync(wallet, wId, appid, false);
        log.info("You have 10 seconds to enter the password :)");
        Thread.sleep(10000L); // give me 10 seconds to enter the password
        assertTrue(handle > 0);
        if (handle > 0) log.info("Wallet " + "'" + Static.DEFAULT_WALLET + "' successfully opened.");
        if (handle > 0) log.info("Received handle: " + handle + ".");
        String folder = "Test-Folder";
        boolean created = kwallet.createFolder(handle, folder, appid);
        assertTrue(created);
        log.info("Folder '" + folder + "' successfully created.");
        boolean removed = kwallet.removeFolder(handle, folder, appid);
        assertTrue(removed);
        log.info("Folder '" + folder + "' successfully deleted.");
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equals("KWallet.walletAsyncOpened")) handle = (int) event.getNewValue();
    }
}
