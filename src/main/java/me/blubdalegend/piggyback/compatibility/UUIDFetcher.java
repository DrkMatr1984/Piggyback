package me.blubdalegend.piggyback.compatibility;

import com.google.common.collect.ImmutableList;

import me.blubdalegend.piggyback.Piggyback;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Interface to Mojang's API to fetch player UUIDs from player names.
 *
 * Thanks to evilmidget38: http://forums.bukkit.org/threads/player-name-uuid-fetcher.250926/
 */

public class UUIDFetcher implements Callable<Map<String, UUID>> {
	private static final double PROFILES_PER_REQUEST = 100;
	private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";
	private final JSONParser jsonParser = new JSONParser();
	private final List<String> names;
	private final boolean rateLimiting;

	private UUIDFetcher(List<String> names, boolean rateLimiting) {
		this.names = ImmutableList.copyOf(names);
		this.rateLimiting = rateLimiting;
	}

	private UUIDFetcher(List<String> names) {
		this(names, true);
	}

    public Map<String, UUID> call() throws Exception {
		Map<String, UUID> uuidMap = new HashMap<String, UUID>();
		int requests = (int) Math.ceil(names.size() / PROFILES_PER_REQUEST);
		for (int i = 0; i < requests; i++) {
			HttpURLConnection connection = createConnection();
			String body = JSONArray.toJSONString(names.subList(i * 100, Math.min((i + 1) * 100, names.size())));
			writeBody(connection, body);
			JSONArray array = (JSONArray) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
			for (Object profile : array) {
				JSONObject jsonProfile = (JSONObject) profile;
				String id = (String) jsonProfile.get("id");
				String name = (String) jsonProfile.get("name");
				UUID uuid = UUIDFetcher.getJSONUUID(id);
				uuidMap.put(name, uuid);
			}
			if (rateLimiting && i != requests - 1) {
				Thread.sleep(100L);
			}
		}
		return uuidMap;
	}

	private static void writeBody(HttpURLConnection connection, String body) throws Exception {
		OutputStream stream = connection.getOutputStream();
		stream.write(body.getBytes());
		stream.flush();
		stream.close();
	}

	private static HttpURLConnection createConnection() throws Exception {
		URL url = new URL(PROFILE_URL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		return connection;
	}

	private static UUID getJSONUUID(String id) {
		return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" +id.substring(20, 32));
	}

	public static void getUUID(Piggyback piggyback, String name, final CallBack callback) {
	    new BukkitRunnable() {
	    	@Override
	        public void run() {
	    		final UUID result = getUUIDInternal(name);
                
	    		Bukkit.getScheduler().runTask(piggyback, new Runnable() {
	                @Override
	                public void run() {
	                    // call the callback with the result
	                    callback.onQueryDone(result);
	                }
	            });        
	    }}.runTaskAsynchronously(piggyback);
	}
	
	private static UUID getUUIDInternal(String name)
    {   
		UUID id = null;
		try {
			id = new UUIDFetcher(Arrays.asList(name)).call().get(name);
		} catch (Exception e) {
			return null;
		}
		return id;
	}
	
	public interface CallBack
	{
		public void onQueryDone(UUID result);
	}
}