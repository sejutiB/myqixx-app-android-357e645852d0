package qix.app.qix;

import android.util.Log;

import org.junit.Test;
import static org.junit.Assert.*;

import qix.app.qix.helpers.CryptoHandler;

public class KaligoTokenGeneratorTest {

    @Test
    public void generateToken(){

        String data = "{\"user_id\":\"75b29ad9-f32e-4cfa-badb-f22ebe75edd2\",\"timestamp\":1542270401127}";
        String token = CryptoHandler.encrypt(data);
        System.out.println(token);

        String payload = CryptoHandler.decrypt(token);

        assertEquals(data, payload);
    }
}
