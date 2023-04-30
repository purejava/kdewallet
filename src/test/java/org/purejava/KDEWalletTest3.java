package org.purejava;

import org.junit.jupiter.api.*;
import org.purejava.kwallet.Static;
import org.purejava.kwallet.KDEWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        var kwallet = new KDEWallet(context.connection);
        var wallet = Static.DEFAULT_WALLET;
        var wId = 0;
        var appid = "Tester";
        var handle = kwallet.open(wallet, wId, appid);
        assertTrue(handle > 0);
        log.info("Wallet '{}' successfully opened.", Static.DEFAULT_WALLET);
        log.info("Received handle: {}.", handle);
        var folder = "Test-Folder";
        var folderCreated = kwallet.createFolder(handle, folder, appid);
        assertTrue(folderCreated);
        log.info("Folder '{}' successfully created.", folder);
        var key1 = "PW1";
        var password = "password";
        var result = kwallet.writePassword(handle, folder, key1, password, appid);
        assertEquals(result, 0);
        if (result == 0) log.info("Secret '{}' with with key '{}' successfully stored in folder '{}'.", password, key1, folder);
        var key2 = "PW2";
        var password2 = "password2";
        result = kwallet.writePassword(handle, folder, key2, password2, appid);
        assertEquals(result, 0);
        if (result == 0) log.info("Secret '{}' with with key '{}' successfully stored in folder '{}'.", password2, key2, folder);
        var key3 = "Map1";
        var map = new byte[]{0,0,0,2,-1,-1,-1,-1,-1,-1,-1,-1,0,0,0,2,0,65,0,0,0,2,0,66};
        result = kwallet.writeMap(handle, folder, key3, map, appid);
        assertEquals(result, 0);
        if (result == 0) log.info("Secret as map with key '{}' successfully stored in folder '{}'.", key3, folder);
        var key4 = "Stream1";
        var stream = "password3".getBytes();
        result = kwallet.writeEntry(handle, folder, key4, stream, appid);
        assertEquals(result, 0);
        if (result == 0) log.info("Secret '{}' with with key '{}' successfully stored in folder '{}'.", new String(stream), key4, folder);
        var el = kwallet.entriesList(handle, folder, appid);
        assertEquals(el.size(),4);
        var ml = kwallet.mapList(handle, folder, appid);
        assertEquals(ml.size(),1);
        var pl = kwallet.passwordList(handle, folder, appid);
        assertEquals(pl.size(),2);
        var p2 = pl.get("PW2");
        assertEquals(p2,"password2");
        log.info("Password list {} contains secret '{}' with key '{}'.", pl, password2, key2);
        result = kwallet.removeEntry(handle, folder, key2, appid);
        assertEquals(result, 0);
        result = kwallet.removeEntry(handle, folder, key1, appid);
        assertEquals(result, 0);
        pl = kwallet.passwordList(handle, folder, appid);
        assertEquals(pl.size(), 0);
        var folderRemoved = kwallet.removeFolder(handle, folder, appid);
        assertTrue(folderRemoved);
        log.info("Folder '{}' successfully deleted.", folder);
        var walletClosedId = kwallet.close(handle, false, appid);
        assertTrue(walletClosedId != -1);
        log.info("Wallet '{}' with handle '{}' successfully closed.", Static.DEFAULT_WALLET, handle);
    }
}
