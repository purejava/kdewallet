package org.purejava;

import org.freedesktop.dbus.Static;
import org.freedesktop.dbus.exceptions.DBusException;
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
    @DisplayName("Testing password functionality in unlocked kdewallet...")
    void testPassword() {
        KDEWallet kwallet = new KDEWallet(context.connection);
        String wallet = Static.DEFAULT_WALLET;
        int wId = 0;
        String appid = "Tester";
        Object[] response = kwallet.send("open", "sxs", wallet, wId, appid);
        int handle = (int) response[0];
        assertTrue(handle > 0);
        if (handle > 0) log.info("Wallet " + "'" + Static.DEFAULT_WALLET + "' successfully opened.");
        if (handle > 0) log.info("Received handle: " + handle + ".");
        String folder = "Test-Folder";
        response = kwallet.send("createFolder", "iss", handle, folder, appid);
        boolean folderCreated = (boolean) response[0];
        assertTrue(folderCreated);
        log.info("Folder '" + folder + "' successfully created.");
        String key = "PW1";
        String value = "secret1";
        response = kwallet.send("writePassword", "issss", handle, folder, key, value, appid);
        int secretStored = (int) response[0];
        assertEquals(secretStored, 0);
        if (secretStored == 0) log.info("Secret '" + value + "' successfully stored.");
        response = kwallet.send("readPassword", "isss", handle, folder, key, appid);
        String secretRead = (String) response[0];
        assertEquals(secretRead, value);
        if (secretRead.equals(value)) log.info("Secret '" + value + "' successfully retrieved.");
        response = kwallet.send("hasEntry", "isss", handle, folder, key, appid);
        boolean keyFound = (boolean) response[0];
        assertTrue(keyFound);
        log.info("Folder '" + folder + "' has key '" + key + "'.");
        response = kwallet.send("removeEntry", "isss", handle, folder, key, appid);
        int keyRemoved = (int) response[0];
        assertEquals(keyRemoved, 0);
        if (keyRemoved == 0) log.info("Key '" + key + "' successfully removed from folder '" + folder +"'.");
        response = kwallet.send("removeFolder", "iss", handle, folder, appid);
        boolean folderRemoved = (boolean) response[0];
        assertTrue(folderRemoved);
        log.info("Folder '" + folder + "' successfully deleted.");
        response = kwallet.send("close", "ibs", handle, false, appid);
        int walletClosed = (int) response[0];
        assertTrue(walletClosed != -1);
        log.info("Wallet '" + Static.DEFAULT_WALLET + "' with handle '" + handle + "' successfully closed.");
    }
}