package com.dev.base.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class JsonUtilsTest {
	
	@Test
	public void testJson() throws Exception{
		String filePath = "src\\test\\resources\\json.txt";
		String content = FileUtils.readFileToString(new File(filePath));
		System.out.println(content);
		System.out.println(content.trim().startsWith("["));
		
		
//		Map<String, Object> json = JsonUtils.toObject(content, Map.class);
//		System.out.println(JsonUtils.toJson(json));
	}
	
	@Test
	public void testRemoveBOM() throws IOException {
		final File dir = new File("src/main/webapp/jsp");
		new DirectoryWalker<File>() {
			{ walk(dir, null); }
			@Override
			protected void handleFile(File file, int depth, Collection<File> results) throws IOException {
				byte[] bs = Files.readAllBytes(file.toPath());
				if (bs[0] == -17 && bs[1] == -69 && bs[2] == -65) {
					System.out.println("remove bom: "+file.getAbsolutePath());
//					byte[] nbs = new byte[bs.length - 3];
//					System.arraycopy(bs, 3, nbs, 0, nbs.length);
//					Files.write(file.toPath(), nbs);
				}
			}
		};
		System.out.println("finish");
	}
}