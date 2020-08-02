package org.purejava;

import org.junit.jupiter.api.*;
import org.kde.Static;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        String key1 = "PW1";
        byte[] password = "password".getBytes();
        response = kwallet.send("writeEntry", "issays", handle, folder, key1, password, appid);
        int result = (int) response[0];
        assertEquals(result, 0);
        if (result == 0) log.info("Secret '" + new String(password) + "' with with key '" + key1 + "' successfully stored in folder '" + folder + "'.");
        String key2 = "Map";
        byte [] map = new byte[]{0,0,0,2,-1,-1,-1,-1,-1,-1,-1,-1,0,0,0,2,0,65,0,0,0,2,0,66};
        MapEntries me = new MapEntries();
        me.setByteField(map);
        response = kwallet.send("writeEntry", "issayis", handle, folder, key2, map, 3, appid);
        result = (int) response[0];
        assertEquals(result, 0);
        if (result == 0) log.info("Map with with key '" + key2 + "' successfully stored in folder '" + folder + "'.");
        response = kwallet.send("entryList", "iss", handle, folder, appid);
        List<String> listOfEntries = (List<String>) response[0];
        assertEquals(listOfEntries.size(), 2);
        assertEquals(listOfEntries.get(0), "Map");
        assertEquals(listOfEntries.get(1), "PW1");
        log.info("Found this list of entries: " + listOfEntries.toString() + ".");
        response = kwallet.send("entryType", "isss", handle, folder, key2, appid);
        int entryType = (int) response[0];
        assertEquals(entryType, 3);
        log.info("Type of entry for map is: " + entryType + ".");
        response = kwallet.send("readEntry", "isss", handle, folder, key1, appid);
        byte[] entry = (byte[]) response[0];
        assertEquals(new String(entry), "password");
        log.info("Raw data from secret successfully retrieved: " + new String(entry) + ".");
        log.info(me.toString());
        assertEquals(me.count(), 2);
        assertTrue(me.hasKey("A"));
        assertTrue(me.hasValue("A","B"));
        me.changeValue("A","C");
        assertTrue(me.hasValue("A","C"));
        log.info("Value successfully changed for key 'A' from 'B' to '" + me.getValue("A") +"'.");
        me.storeEntry("D",null);
        assertTrue(me.hasKey("D"));
        assertTrue(me.hasValue("D",""));
        log.info("Entry with key 'D' and value '' successfully stored.");
        log.info(me.toString());
        assertEquals(me.count(),3);
        me.removeEntry("A","C");
        me.removeEntry("D","");
        assertFalse(me.hasKey("D"));
        assertFalse(me.hasValue("D", ""));
        assertEquals(me.count(), 1);
        log.info("Entries with keys 'A' and 'D' successfully removed.");
        response = kwallet.send("writeEntry", "issayis", handle, folder, key2, me.getByteField(), 3, appid);
        result = (int) response[0];
        assertEquals(result, 0);
        if (result == 0) log.info("Map with with key '" + key2 + "' successfully updated in folder '" + folder + "'.");
        String newName = "newName";
        response = kwallet.send("renameEntry", "issss", handle, folder, key1, newName, appid);
        int renaming = (int) response[0];
        assertTrue(renaming != -1);
        log.info("Entry successfully renamed to: '" + newName + "'.");
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