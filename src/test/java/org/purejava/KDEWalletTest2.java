package org.purejava;

import org.kde.Static;
import org.junit.jupiter.api.*;
import org.kde.KWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KDEWalletTest2 {
    private final Logger log = LoggerFactory.getLogger(KDEWalletTest2.class);
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
    @Order(5)
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
        kwallet.close(handle, true, appid);
        kwallet.getSignalHandler().await(KWallet.walletClosedInt.class, Static.ObjectPaths.KWALLETD5, () -> null);
        int close_handle = kwallet.getSignalHandler().getLastHandledSignal(KWallet.walletClosedInt.class, Static.ObjectPaths.KWALLETD5).handle;
        assertEquals(handle, close_handle);
        log.info("Wallet '" + Static.DEFAULT_WALLET + "' with handle '" + close_handle + "' successfully closed.");
    }
}
