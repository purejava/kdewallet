package org.purejava;

import org.freedesktop.dbus.exceptions.DBusException;
import org.kde.Static;
import org.junit.jupiter.api.*;
import org.kde.KWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KDEWalletTest {
    private final Logger log = LoggerFactory.getLogger(KDEWalletTest.class);
    private Context context;

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
            context.connection.getRemoteObject("org.kde.kwalletd5",
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
    @DisplayName("Testing create folder functionality in locked kdewallet...")
    void testWriteEntry() {
        KDEWallet kwallet = new KDEWallet(context.connection);
        String wallet = Static.DEFAULT_WALLET;
        int wId = 0;
        String appid = "Tester";
        kwallet.openAsync(wallet, wId, appid, false);
        kwallet.getSignalHandler().await(KWallet.walletAsyncOpened.class, Static.ObjectPaths.KWALLETD5, () -> null);
        int handle = kwallet.getSignalHandler().getLastHandledSignal(KWallet.walletAsyncOpened.class, Static.ObjectPaths.KWALLETD5).handle;
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
}
