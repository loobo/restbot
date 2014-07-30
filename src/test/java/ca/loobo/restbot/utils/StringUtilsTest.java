package ca.loobo.restbot.utils;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.springframework.web.util.UriComponentsBuilder;

public class StringUtilsTest {

	public boolean multilineMatches(String input, String regex) {
	    Pattern p = Pattern.compile(regex, Pattern.MULTILINE);

	    Matcher m = p.matcher(input);

	    return m.matches();

	}
	
	@Test
	public void testMultilineMatching() {
		String str = "(HD) In a magical world, the land of Jhamora is torn apart by the mutual prejudice of two peoples. nonccSSAndHLSNonPR1 joins forces with a sworn enemy to save the land. (Animated)";
		assertTrue(str.matches(".*Jhamora.*nonccSSAndHLSNonPR1.*"));
		
		str = "FR-Movie title cp0000-tiMov400000\nÀ, Â, Æ, Ç, Î, Ï, Ô, Œ, Ù, Û, Ü, Ÿ\nà, â, æ, ç, î, ï,ô, œ, ù, û, ü, ÿ";
		
		//multi-line match is not supported by String
		assertFalse(str.matches(".*Movie.*title.*cp0000.*tiMov400000.*"));
		assertFalse(multilineMatches(str, ".*Movie.*title.*cp0000.*tiMov400000.*"));
	}
	
	@Test
	public void testPathMatching() {
		String value = "c:\\work\\projects\\catalog-api-test\\src\\main\\resources";
		assertTrue(value.matches("[a-zA-Z]{1}:\\\\.*"));
	}
	
	@Test
	public void testStringCatenationSpeed() {
        long now = System.currentTimeMillis();
        slow();
        System.out.println("slow elapsed " + (System.currentTimeMillis() - now) + " ms");

        now = System.currentTimeMillis();
        fast();
        System.out.println("fast elapsed " + (System.currentTimeMillis() - now) + " ms");		
	}


    private void fast()
    {
        for(int i=0;i<100000000;i++) {
            fastCat("url", "res", "id");
        }
    }

    private void slow()
    {
        for(int i=0;i<100000000;i++) {
            slowCat("url", "res", "id");
        }
    }
    
    private String slowCat(String url, String res, String id) {
    	return url + "/" + res + "/" + id;
    }
    
    private String fastCat(String url, String res, String id) {
    	StringBuilder s = new StringBuilder(url).append("/").append(res).append("/").append(id);
    	return s.toString();
    }
    
    @Test
    public void uriTest() {
    	UriComponentsBuilder b = UriComponentsBuilder.fromUriString("?a=1 &b=2");
    	b.pathSegment("a","b");
    	b.pathSegment("c");
    	b.path("cc");
    	b.scheme("http");
    	b.replaceQuery("i=3&y=4");
    	b.host("www.aa.com");
    	String url = b.build().toUriString();
    	System.err.println(url);
    }
}
