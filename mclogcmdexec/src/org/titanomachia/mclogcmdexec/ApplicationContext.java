package org.titanomachia.mclogcmdexec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.jgdk.util.DataFeedReader;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class ApplicationContext {
	private static Map<String, Object> DATA = new HashMap<String, Object>();
	
	private static String filePath;

	private static CommandFactory factory;
	
	public static void setFilePath(String filePath) {
		ApplicationContext.filePath = filePath;
	}
	
	public static void load() {
		File file = new File(filePath);
		if (!file.exists()) {
			System.err.println("Unable to load context from \"" + filePath + "\"");
			return;
		}
		
		try {
			DataFeedReader reader = new DataFeedReader(new FileReader(file)) {
				@Override
				protected void processData(String line) {
					String key = line.substring(0, line.indexOf('='));
					String value = line.substring(line.indexOf('=') + 2, line.length() - 1);
					BASE64Decoder decoder = new BASE64Decoder();
					try {
						byte[] bytes = decoder.decodeBuffer(value);
						ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(bytes));
						DATA.put(key, stream.readObject());
					} catch (IOException e) {
						System.err.println("Failed to decode line " + e);
					} catch (ClassNotFoundException e) {
						System.err.println("Failed to find class " + e);
					}
				}

				@Override
				protected String readLine() throws IOException {
					String line = super.readLine();
					while (null != line && !line.endsWith("\"")) {
						line = line + super.readLine();
					}
					return line;
				}
			};
			
			reader.run();
		} catch (FileNotFoundException e) {
			System.err.println("Unable to read from \"" + filePath + "\"");
		}
	}
	
	public static void save() {
		File file = new File(filePath);
		if (!file.exists()) {
			System.out.println("Creating context at \"" + filePath + "\"");
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.err.println("Unable to create context at \"" + filePath + "\"");
			}
		}

		FileWriter writer = null;
		try {
			writer = new FileWriter(file);

			for(String key : DATA.keySet()) {
				Object value = DATA.get(key);
				ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
				ObjectOutputStream stream = new ObjectOutputStream(byteStream);
				stream.writeObject(value);
				byte[] bytes = byteStream.toByteArray();
				BASE64Encoder encoder = new BASE64Encoder();
				String line = key + "=" + "\"" + encoder.encode(bytes) + "\"";
				writer.write(line + System.getProperty("line.separator"));
			}
		} catch (IOException e) {
			System.err.println("Failed to open file for writing " + e);
		}
		finally {
			if (null != writer) {
				try {
					writer.close();
				} catch (IOException e) {
					System.err.println("Failed to close context file " + e);
				}
			}
		}
	}
	
	public static <T> T getValue(String key) {
		@SuppressWarnings("unchecked")
		T t = (T)DATA.get(key);
		return t;
	}
	
	public static void setValue(String key, Object value) {
		DATA.put(key, value);
	}

	public static void clearValue(String key) {
		DATA.remove( key );
	}

	public static void setCommandFactory(CommandFactory factory) {
		ApplicationContext.factory = factory;
	}

	public static CommandFactory getFactory() {
		return factory;
	}
}
