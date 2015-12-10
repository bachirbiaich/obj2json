package test.java.obj2json;
import main.java.fr.iut.obj2json.Obj2JsonConverter;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;


public class Obj2JsonConverterTest {

    @Test
    public void testConvertObj() throws Exception {
        String res = Obj2JsonConverter.convertObject(new MockObj());
        Assert.assertNotNull(res);
        System.out.println(res);
        String expected = new String(Files.readAllBytes(Paths.get("src/test/resources/expected-MockObj.txt")));
        Assert.assertEquals(expected, res);
    }
}
