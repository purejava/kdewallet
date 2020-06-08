package org.purejava;

import org.freedesktop.dbus.Static;
import org.freedesktop.dbus.exceptions.DBusException;
import org.junit.jupiter.api.*;
import org.kde.test.KWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KDEWalletTest
{
    private final Logger log = LoggerFactory.getLogger(KDEWalletTest.class);
    private Context context;

    @BeforeEach
    public void beforeEach(TestInfo info) {
        log.info(info.getDisplayName());
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
            KWallet kwallet = context.connection.getRemoteObject("org.kde.kwalletd5",
                    "/modules/kwalletd5", KWallet.class);
            log.info("Kwallet daemon is available.");
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
    @DisplayName("Opening kdewallet...")
    void open() {
        KDEWallet kwallet = new KDEWallet(context.connection);
        String wallet = Static.DEFAULT_WALLET;
        int wId = 0;
        String appid = "Tester";
        Object[] response = kwallet.send("open", "sxs", wallet, wId, appid);
        int handle = (int) response[0];
        assertTrue(handle > 0);
        log.info("Wallet " + "'" + Static.DEFAULT_WALLET + "' successfully opened and unlocked.");
        log.info("Received handle: " + handle);
    }

    @Test
    @Order(4)
    @DisplayName("Opening kdewallet asynchronously...")
    void openAsync() {
        KDEWallet kwallet = new KDEWallet(context.connection);
        String wallet = Static.DEFAULT_WALLET;
        int wId = 0;
        String appid = "Tester";
        boolean handleSession = false;
        Object[] response = kwallet.send("openAsync", "sxsb", wallet, wId, appid, handleSession);
        int transactionID = (int) response[0];
        assertTrue(transactionID > 0);
        log.info("Wallet " + "'" + Static.DEFAULT_WALLET + "'" + " successfully opened asynchronously.");
        log.info("Received transactionID: " + transactionID);
    }
}