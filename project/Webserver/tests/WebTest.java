package tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static webserver.Webserver.rootFolder;

public class WebTest {
//	public static void main(String[] args) {
//        // declaration and instantiation of objects/variables
//    	System.setProperty("webdriver.gecko.driver","/usr/bin/geckodriver");
//		WebDriver driver = new FirefoxDriver();
//		//comment the above 2 lines and uncomment below 2 lines to use Chrome
//		//System.setProperty("webdriver.chrome.driver","G:\\chromedriver.exe");
//		//WebDriver driver = new ChromeDriver();
//    	
//        String baseUrl = "localhost:10008";
//        String expectedTitle = "index1 title";
//        String actualTitle = "";
//
//        // launch Fire fox and direct it to the Base URL
//        driver.get(baseUrl);
//        
//        // get the actual value of the title
//        actualTitle = driver.getTitle();
//
//        /*
//         * compare the actual title of the page with the expected one and print
//         * the result as "Passed" or "Failed"
//         */
//        if (actualTitle.contentEquals(expectedTitle)){
//            System.out.println("Test Passed!");
//        } else {
//            System.out.println("Test Failed");
//        }
//       
//        //close Fire fox
//        driver.close();
//       
//    }
	
	private WebDriver driver;
	
	@BeforeClass
	public static void fstInit() {
		System.setProperty("webdriver.gecko.driver","/usr/bin/geckodriver"); //creez contextul si var globale
	}
	
	@Before
	public void init() {
		driver = new FirefoxDriver(); 
	}
	
	@After
	public void destory() {
		driver.close();
	}
	
	@Test
	public void rootTest(){
		try {
			driver.get("localhost:10008");
			String title = driver.getTitle();
			Assert.assertEquals(title, "index1 title"); //testam ca pagina este buna
			
			//ma gandesc ca pana aici este destul de ok etstul, practic, daca nu exista pagini cu acelasi titlu, ar trebui sa fie incarcat documentul complet
			//m-am gandit totusi sa verific daca continutul este acelasi
			//din cauza ca spatiile si caracterul new line nu erau "aliniate" in textul primit si cel citit din fisier, le-ma eliminat, acest lucru afectand textul (ma refer la textul ce apare pe ecran)
			//pe de alta parte, eu nu verific aici ca textul este corect, ci ca s-a incarcat pagina buna
			//totusi consiger ca acest lucru nu este necesar, iar pe viitor nu voi mai verifica continutul - doar titlul, sper ca este o decizie buna
			
			//testez continutul
			BufferedReader fileIn = new BufferedReader(new FileReader(new File(rootFolder+"/index.html")));
			char[] got = driver.getPageSource().replaceAll("[ \n]", "").toCharArray(); //am facut cateva "smecheri" pentru a testa ca s-a incarcat prima pagina corect
			int expected, i=0;
			char c;
			// fileIn.readLine(); // din nu stiu ce motice nu se transmite linia <!DOCTYPE html> - de rezolvat // solved - changed in /index.html <!DOCTYPE html> with <html>...</html>
			while((expected =fileIn.read()) != -1) {
				c=(char)expected;
				if(c == '\n' || c == ' ')
					continue;
				Assert.assertEquals(c, got[i]);
				i++;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
	}
	
	//2 teste daca functioneaza <a href=""></a>
	@Test
	public void a_b_html_pageLoadFromIndexLinkClick() {
		//acces "localhost:10008" then try to get to "a b"
		try {
			driver.get("localhost:10008");
			
			driver.findElement(By.linkText("a b")).click();
			
			Assert.assertEquals("/a b", driver.getTitle());
		}catch(Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void index_a_b_a_link() {
		//acces "localhost:10008" then try to get to "a.html" then to "a b.html"
		try {
			driver.get("localhost:10008");
			
			driver.findElement(By.linkText("a b")).click();
			driver.findElement(By.linkText("back")).click();
			
			Assert.assertEquals("Welcome!", driver.getTitle());
		}catch(Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	//test input text box with submit button that clears the input
	@Test
	public void verifyInputTextBox() {
		try {
			driver.get("localhost:10008/a b.html");
			WebElement e = driver.findElement(By.id("idimp"));
			Assert.assertEquals("", e.getAttribute("value"));
			e.sendKeys("id-test");
			Assert.assertEquals("id-test", e.getAttribute("value"));
		}catch(Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void verifyInputTextBoxAndClearButton() { //in html clear button e butonul de submit id
		try {
			driver.get("localhost:10008/a b.html");
			WebElement e = driver.findElement(By.id("idimp"));
			Assert.assertEquals("", e.getAttribute("value"));
			e.sendKeys("id-test");
			Assert.assertEquals("id-test", e.getAttribute("value"));
			driver.findElement(By.name("clickButton")).click();
			Assert.assertEquals("", e.getAttribute("value"));
		}catch(Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
