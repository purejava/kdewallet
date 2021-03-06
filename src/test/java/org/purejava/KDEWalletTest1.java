package org.purejava;

import org.junit.jupiter.api.*;
import org.kde.Static;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KDEWalletTest1 {
    private final Logger log = LoggerFactory.getLogger(KDEWalletTest1.class);
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
    @Order(4)
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
        int walletClosedId = (int) response[0];
        assertTrue(walletClosedId != -1);
        log.info("Wallet '" + Static.DEFAULT_WALLET + "' with handle '" + handle + "' successfully closed.");
    }
}