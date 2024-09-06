package me.blubdalegend.piggyback.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.plugin.java.JavaPlugin;

// Credit goes to alexey-va for this code

public final class ResourceUtils {

	public static List<String> listFiles(Class<?> clazz, String path) throws IOException {
	    List<String> files = new ArrayList<String>();

	    URL url = clazz.getResource(path);
		if (url == null)
			throw new IllegalArgumentException("list files failed for path '" + path + "'.");
	    if ("file".equals(url.getProtocol())) {
            try {
		         File rootDir = new File(url.toURI());
		         findFilesRecursive(files, rootDir, rootDir);
		         return files;
		    } catch (URISyntaxException e) {
		        throw new AssertionError(e); // should never happen
		    }
		}

		if ("jar".equals(url.getProtocol())) {
		    if (path.startsWith("/"))
		    	path = path.substring(1);
		    String jarFile = url.getPath().substring(5, url.getPath().indexOf("!"));
		    JarFile jar = new JarFile(jarFile);
		    Enumeration<JarEntry> entries = jar.entries();
		    while (entries.hasMoreElements()) {
		        JarEntry entry = entries.nextElement();
		        if (!entry.isDirectory()) {
		            String name = entry.getName();
		            if (name.startsWith(path))
		                 files.add(name.substring(path.length() + 1));
		        }
		    }
            
		    jar.close();
		    return files;
		}
	    throw new IllegalArgumentException("Unexpected protocol: " + url.getProtocol());
	}

	private static void findFilesRecursive(List<String> files, File rootDir, File currPath) {
		if (currPath.isDirectory()) {
			for (File path : currPath.listFiles()) {
				findFilesRecursive(files, rootDir, path);
			}
		}
		else {
			files.add(currPath.getAbsolutePath().substring(rootDir.getAbsolutePath().length() + 1));
		}
	}
	
	public static List<String> listLibFiles(JavaPlugin plugin) {
        List<String> fileList = new ArrayList<>();
        Path path = Paths.get(plugin.getDataFolder().getAbsolutePath() + System.getProperty("file.separator") + "lib");
        // Use DirectoryStream to iterate over the directory contents
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream) {
                // Add the file names to the list
                fileList.add(entry.getFileName().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileList;
    }
}