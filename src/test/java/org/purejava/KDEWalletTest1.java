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
        var kwallet = new KDEWallet(context.connection);
        var wallet = Static.DEFAULT_WALLET;
        var wId = 0;
        var appid = "Tester";
        var response = kwallet.send("open", "sxs", wallet, wId, appid);
        assertTrue(kwallet.isOpen(wallet));
        var handle = (int) response[0];
        assertTrue(handle > 0);
        if (handle > 0) log.info("Wallet '{}' successfully opened.", Static.DEFAULT_WALLET);
        if (handle > 0) log.info("Received handle: {}.", handle);
        var folder = "Test-Folder";
        response = kwallet.send("createFolder", "iss", handle, folder, appid);
        var folderCreated = (boolean) response[0];
        assertTrue(folderCreated);
        log.info("Folder '{}' successfully created.", folder);
        var key = "PW1";
        var value = "secret1";
        response = kwallet.send("writePassword", "issss", handle, folder, key, value, appid);
        var secretStored = (int) response[0];
        assertEquals(secretStored, 0);
        if (secretStored == 0) log.info("Secret '{}' successfully stored.", value);
        response = kwallet.send("readPassword", "isss", handle, folder, key, appid);
        var secretRead = (String) response[0];
        assertEquals(secretRead, value);
        if (secretRead.equals(value)) log.info("Secret '{}' successfully retrieved.", value);
        response = kwallet.send("hasEntry", "isss", handle, folder, key, appid);
        var keyFound = (boolean) response[0];
        assertTrue(keyFound);
        log.info("Folder '{}' has key '{}'.", folder, key);
        response = kwallet.send("removeEntry", "isss", handle, folder, key, appid);
        var keyRemoved = (int) response[0];
        assertEquals(keyRemoved, 0);
        if (keyRemoved == 0) log.info("Key '{}' successfully removed from folder '{}'.", key, folder);
        response = kwallet.send("removeFolder", "iss", handle, folder, appid);
        var folderRemoved = (boolean) response[0];
        assertTrue(folderRemoved);
        log.info("Folder '{}' successfully deleted.", folder);
        response = kwallet.send("close", "ibs", handle, false, appid);
        var walletClosedId = (int) response[0];
        assertTrue(walletClosedId != -1);
        log.info("Wallet '{}' with handle '{}' successfully closed.", Static.DEFAULT_WALLET, handle);
    }
}