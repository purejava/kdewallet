package org.purejava;

import org.junit.jupiter.api.*;
import org.kde.Static;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KDEWalletTest3 {
    private final Logger log = LoggerFactory.getLogger(KDEWalletTest3.class);
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
    @Order(6)
    @DisplayName("Testing entries list functionalities in unlocked kdewallet...")
    void testWriteEntry() {
        KDEWallet kwallet = new KDEWallet(context.connection);
        String wallet = Static.DEFAULT_WALLET;
        int wId = 0;
        String appid = "Tester";
        int handle = kwallet.open(wallet, wId, appid);
        assertTrue(handle > 0);
        log.info("Wallet " + "'" + Static.DEFAULT_WALLET + "' successfully opened.");
        log.info("Received handle: " + handle + ".");
        String folder = "Test-Folder";
        boolean folderCreated = kwallet.createFolder(handle, folder, appid);
        assertTrue(folderCreated);
        log.info("Folder '" + folder + "' successfully created.");
        String key1 = "PW1";
        String password = "password";
        int result = kwallet.writePassword(handle, folder, key1, password, appid);
        assertEquals(result, 0);
        if (result == 0) log.info("Secret '" + password + "' with with key '" + key1 + "' successfully stored in folder '" + folder + "'.");
        String key2 = "PW2";
        String password2 = "password2";
        result = kwallet.writePassword(handle, folder, key2, password2, appid);
        assertEquals(result, 0);
        if (result == 0) log.info("Secret '" + password2 + "' with with key '" + key2 + "' successfully stored in folder '" + folder + "'.");
        String key3 = "Map1";
        byte [] map = new byte[]{0,0,0,2,-1,-1,-1,-1,-1,-1,-1,-1,0,0,0,2,0,65,0,0,0,2,0,66};
        result = kwallet.writeMap(handle, folder, key3, map, appid);
        assertEquals(result, 0);
        if (result == 0) log.info("Secret as map with key '" + key3 + "' successfully stored in folder '" + folder + "'.");
        String key4 = "Stream1";
        byte[] stream = "password3".getBytes();
        Object[] response = kwallet.send("writeEntry", "issays", handle, folder, key4, stream, appid);
        result = (int) response[0];
        assertEquals(result, 0);
        if (result == 0) log.info("Secret '" + new String(stream) + "' with with key '" + key4 + "' successfully stored in folder '" + folder + "'.");
        Map<String, byte[]> el = kwallet.entriesList(handle, folder, appid);
        assertEquals(el.size(),4);
        Map<String, byte[]> ml = kwallet.mapList(handle, folder, appid);
        assertEquals(ml.size(),1);
        Map<String, String> pl = kwallet.passwordList(handle, folder, appid);
        assertEquals(pl.size(),2);
        String p2 = pl.get("PW2");
        assertEquals(p2,"password2");
        log.info("Password list " + pl.toString() + " contains secret '" + password2 + "' with key '" + key2 + "'.");
        result = kwallet.removeEntry(handle, folder, key2, appid);
        assertEquals(result, 0);
        result = kwallet.removeEntry(handle, folder, key1, appid);
        assertEquals(result, 0);
        pl = kwallet.passwordList(handle, folder, appid);
        assertEquals(pl.size(), 0);
        boolean folderRemoved = kwallet.removeFolder(handle, folder, appid);
        assertTrue(folderRemoved);
        log.info("Folder '" + folder + "' successfully deleted.");
        int walletClosedId = kwallet.close(handle, false, appid);
        assertTrue(walletClosedId != -1);
        log.info("Wallet '" + Static.DEFAULT_WALLET + "' with handle '" + handle + "' successfully closed.");
    }
}
