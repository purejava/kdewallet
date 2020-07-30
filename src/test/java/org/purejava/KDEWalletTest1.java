package org.purejava;

import org.kde.Static;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
    @DisplayName("Testing write entry functionality in unlocked kdewallet...")
    void testWriteEntry() {
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
        byte[] password = "password".getBytes();
        response = kwallet.send("writeEntry", "issays", handle, folder, key, password, appid);
        int result = (int) response[0];
        assertEquals(result, 0);
        if (result == 0) log.info("Secret '" + new String(password) + "' with with key '" + key + "' successfully stored in folder '" + folder + "'.");
        response = kwallet.send("entryList", "iss", handle, folder, appid);
        List<String> listOfEntries = (List<String>) response[0];
        assertEquals(listOfEntries.size(), 1);
        assertEquals(listOfEntries.get(0), "PW1");
        log.info("Found this list of entries: " + listOfEntries.toString() + ".");
        response = kwallet.send("entryType", "isss", handle, folder, key, appid);
        int entryType = (int) response[0];
        assertEquals(entryType, 2);
        log.info("Type of entry is: " + entryType + ".");
        response = kwallet.send("readEntry", "isss", handle, folder, key, appid);
        byte[] entry = (byte[]) response[0];
        assertEquals(new String(entry), "password");
        log.info("Raw data from secret successfully retrieved: " + new String(entry) + ".");
        String newName = "newName";
        response = kwallet.send("renameEntry", "issss", handle, folder, key, newName, appid);
        int renaming = (int) response[0];
        assertTrue(renaming != -1);
        log.info("Folder successfully renamed to: '" + newName + "'.");
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