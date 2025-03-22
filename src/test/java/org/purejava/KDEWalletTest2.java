package org.purejava;

import org.junit.jupiter.api.*;
import org.purejava.kwallet.Static;
import org.purejava.kwallet.KDEWallet;
import org.purejava.kwallet.MapEntries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class KDEWalletTest2 {
    private static final Logger LOG = LoggerFactory.getLogger(KDEWalletTest2.class);
    private Context context;

    @BeforeEach
    public void beforeEach(TestInfo info) {
        context = new Context();
        context.ensureService();
    }

    @AfterEach
    public void afterEach() {
        context.after();
    }

    @Test
    @Order(5)
    @DisplayName("Testing write entry functionality in unlocked kdewallet...")
    void testWriteEntry() {
        var kwallet = new KDEWallet(context.connection);
        var wallet = Static.DEFAULT_WALLET;
        var wId = 0;
        var appid = "Tester";
        var response = kwallet.send("open", "sxs", wallet, wId, appid);
        var handle = (int) response[0];
        assertTrue(handle > 0);
        LOG.info("Wallet '{}' successfully opened.", Static.DEFAULT_WALLET);
        LOG.info("Received handle: {}.", handle);
        var folder = "Test-Folder";
        response = kwallet.send("createFolder", "iss", handle, folder, appid);
        var folderCreated = (boolean) response[0];
        assertTrue(folderCreated);
        LOG.info("Folder '{}' successfully created.", folder);
        var key1 = "PW1";
        var password = "password".getBytes();
        response = kwallet.send("writeEntry", "issays", handle, folder, key1, password, appid);
        var result = (int) response[0];
        assertEquals(result, 0);
        if (result == 0) LOG.info("Secret '{}' with with key '{}' successfully stored in folder '{}'.", new String(password), key1, folder);
        var key2 = "Map";
        var map = new byte[]{0,0,0,2,-1,-1,-1,-1,-1,-1,-1,-1,0,0,0,2,0,65,0,0,0,2,0,66};
        var me = new MapEntries();
        me.setByteField(map);
        response = kwallet.send("writeEntry", "issayis", handle, folder, key2, map, 3, appid);
        result = (int) response[0];
        assertEquals(result, 0);
        if (result == 0) LOG.info("Map with with key '{}' successfully stored in folder '{}'.", key2, folder);
        response = kwallet.send("entryList", "iss", handle, folder, appid);
        var listOfEntries = (List<String>) response[0];
        assertEquals(listOfEntries.size(), 2);
        assertEquals(listOfEntries.get(0), "Map");
        assertEquals(listOfEntries.get(1), "PW1");
        LOG.info("Found this list of entries: {}.", listOfEntries);
        response = kwallet.send("entryType", "isss", handle, folder, key2, appid);
        var entryType = (int) response[0];
        assertEquals(entryType, 3);
        LOG.info("Type of entry for map is: {}.", entryType);
        response = kwallet.send("readEntry", "isss", handle, folder, key1, appid);
        var objectList = (List<?>) response[0];
        var entry = new byte[objectList.size()];
        IntStream.range(0, objectList.size()).forEach(i -> entry[i] = (Byte) objectList.get(i));
        assertEquals(new String(entry), "password");
        LOG.info("Raw data from secret successfully retrieved: {}.", new String(entry));
        LOG.info(me.toString());
        assertEquals(me.count(), 2);
        assertTrue(me.hasKey("A"));
        assertTrue(me.hasValue("A","B"));
        me.changeValue("A","C");
        assertTrue(me.hasValue("A","C"));
        LOG.info("Value successfully changed for key 'A' from 'B' to '{}'.", me.getValue("A"));
        me.storeEntry("D",null);
        assertTrue(me.hasKey("D"));
        assertTrue(me.hasValue("D",""));
        LOG.info("Entry with key 'D' and value '' successfully stored.");
        LOG.info(me.toString());
        assertEquals(me.count(),3);
        me.removeEntry("A","C");
        me.removeEntry("D","");
        assertFalse(me.hasKey("D"));
        assertFalse(me.hasValue("D", ""));
        assertEquals(me.count(), 1);
        LOG.info("Entries with keys 'A' and 'D' successfully removed.");
        response = kwallet.send("writeEntry", "issayis", handle, folder, key2, me.getByteField(), 3, appid);
        result = (int) response[0];
        assertEquals(result, 0);
        if (result == 0) LOG.info("Map with with key '{}' successfully updated in folder '{}'.", key2, folder);
        var longString = Stream.iterate("A", s -> s).limit(64).collect(Collectors.joining());
        me.storeEntry("E", longString);
        response = kwallet.send("writeEntry", "issayis", handle, folder, key2, me.getByteField(), 3, appid);
        result = (int) response[0];
        assertEquals(result, 0);
        response = kwallet.send("readMap", "isss", handle, folder, key2, appid);
        var objectListFromResponse = (List<?>) response[0];
        var rm = new byte[objectListFromResponse.size()];
        IntStream.range(0, objectListFromResponse.size()).forEach(i -> rm[i] = (Byte) objectListFromResponse.get(i));
        me.setByteField(rm);
        assertTrue(me.hasValue("E", longString));
        LOG.info("Successfully stored and retrieved value '{}' with a length gt 63.", me.getValue("E"));
        var newName = "newName";
        response = kwallet.send("renameEntry", "issss", handle, folder, key1, newName, appid);
        var renaming = (int) response[0];
        assertTrue(renaming != -1);
        LOG.info("Entry successfully renamed to: '{}'.", newName);
        response = kwallet.send("removeFolder", "iss", handle, folder, appid);
        var folderRemoved = (boolean) response[0];
        assertTrue(folderRemoved);
        LOG.info("Folder '{}' successfully deleted.", folder);
        response = kwallet.send("close", "ibs", handle, false, appid);
        var walletClosedId = (int) response[0];
        assertTrue(walletClosedId != -1);
        LOG.info("Wallet '{}' with handle '{}' successfully closed.", Static.DEFAULT_WALLET, handle);
    }
}