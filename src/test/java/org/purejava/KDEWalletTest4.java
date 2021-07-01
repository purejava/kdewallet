package org.purejava;

import org.junit.jupiter.api.*;
import org.kde.Static;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static org.junit.jupiter.api.Assertions.*;

public class KDEWalletTest4 implements PropertyChangeListener {
    private final Logger log = LoggerFactory.getLogger(KDEWalletTest4.class);
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
    @Order(7)
    @DisplayName("Checking availability of signals for locked wallet ...")
    public void testSignals() throws InterruptedException {
        KDEWallet kwallet = new KDEWallet(context.connection);
        String wallet = Static.DEFAULT_WALLET;
        int wId = 0;
        String appid = "Tester";
        kwallet.getSignalHandler().addPropertyChangeListener(this);
        assertFalse(kwallet.isOpen(wallet));
        kwallet.openAsync(wallet, wId, appid, false);
        log.info("You have 10 seconds to enter the password :)");
        Thread.sleep(10000L);
        assertTrue(handle > 0);
        log.info("Wallet " + "'" + Static.DEFAULT_WALLET + "' successfully opened.");
        int walletClosedId = kwallet.close(handle, true, appid);
        assertTrue(walletClosedId != -1);
        log.info("Wallet '" + Static.DEFAULT_WALLET + "' with handle '" + handle + "' successfully closed.");
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case "KWallet.walletAsyncOpened":
                handle = (int) event.getNewValue();
                break;
            case "KWallet.walletClosedId":
                int closeHandle = (int) event.getNewValue();
                assertEquals(closeHandle, handle);
                log.info("Received signal 'KWallet.walletClosedId' with handle '" + closeHandle + "'.");
                break;
            case "KWallet.walletClosed":
                try {
                    String wallet = (String) event.getNewValue();
                    assertEquals(wallet, Static.DEFAULT_WALLET);
                    log.info("Received signal 'KWallet.walletClosed' for wallet '" + wallet + "'.");
                } catch (ClassCastException ex) {
                    // ToDo handling overloaded signal here. Remove, when walletClosed(String path, int handle) got removed from kwallet.
                }
                break;
            default:
                break;
        }
    }
}