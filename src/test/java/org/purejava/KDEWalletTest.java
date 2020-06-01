package org.purejava;

import org.freedesktop.dbus.Static;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KDEWalletTest
{
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
    @DisplayName("Checking availiability of kdewallet...")
    public void wallets() {
        KDEWallet kwallet = new KDEWallet(context.connection);
        Object[] response = kwallet.send("wallets");
        assertEquals(1, response.length);
        List<String> walletNames = (List<String>) response[0];
        assertEquals(walletNames.get(0), Static.DEFAULT_WALLET);
        log.info("Found: " + walletNames.get(0));
    }
}