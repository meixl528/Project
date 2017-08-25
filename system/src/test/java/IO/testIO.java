package IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

@SuppressWarnings({ "unused", "resource" })
public class testIO {
	
	public static void main(String[] args) throws FileNotFoundException {
		File file = new File("aa.text");

		//字节流
		FileInputStream fileInputStream = new FileInputStream(file);
		//字符流
		InputStreamReader inputStreamReader = new FileReader(file);

		//抽象类
		InputStream inputStream = new FileInputStream(file);
		OutputStream outputStream = new FileOutputStream(file);
		
		
		//抽象类
		Reader reader = new InputStreamReader(new FileInputStream(file));
		Writer writer = new OutputStreamWriter(new FileOutputStream(file));

	}
}
