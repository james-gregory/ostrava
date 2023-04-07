package com.ostrava;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.*;

@Slf4j
@PluginDescriptor(
	name = "Ostrava"
)
public class OstravaPlugin extends Plugin
{
	private long startTime = 0;
	private long[] pauseTimes = new long[0];
	private long[] resumeTimes = new long[0];
	private long stopTime = 0;

	WorldPoint playerPos;

	OstravaPanel opanel;

	private boolean recording = false;

	private boolean paused = false;

	private FileWriter file;
	@Inject
	private Client client;

	@Inject
	private OstravaConfig config;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Ostrava started!");
		opanel = new OstravaPanel(this);
		NavigationButton navButton = NavigationButton.builder()
				.tooltip("Ostrava Record")
				.icon(ImageUtil.getResourceStreamFromClass(getClass(), "/icon.png"))
				.priority(70)
				.panel(opanel)
				.build();

		clientToolbar.addNavigation(navButton);
	}

	@Inject
	private ClientToolbar clientToolbar;

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick) {
		if (recording && !paused) {
			playerPos = client.getLocalPlayer().getWorldLocation();
			String line = String.format("%s, %s, %s", System.currentTimeMillis() - startTime, playerPos.getX(), playerPos.getY());
			log.info(line);
			writeToFile(line);
		}
	}

	public void record() {
		if (!recording) startRecording();
		else stopRecording();
		opanel.update(recording, paused);
	}

	public void startRecording() {
		WorldPoint initialPlayerPos = client.getLocalPlayer().getWorldLocation();
		if (initialPlayerPos == null) {
			return;
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
		String currentDateAndTime = dateFormat.format(new Date());
		String filename = "recordings/ostrava_" + currentDateAndTime + ".csv";

		try {
			file = new FileWriter(filename);
		} catch (IOException e) {
			System.out.println("Error occurred: " + e.getMessage());
			return;
		}

		startTime = System.currentTimeMillis();
		// TODO: create file with start time and pos as first row
		recording = true;
	}

	public void stopRecording() {
		stopTime = System.currentTimeMillis();
		recording = false;
		paused = false;
		try {
			// TODO: print final time to file
			file.close();
		} catch (IOException e) {
			System.out.println("Error occurred: " + e.getMessage());
		}
	}

	public void pause() {
		if (!recording) return;
		if (paused) {
			resumeTimes = appendToArray(resumeTimes, System.currentTimeMillis());
			// TODO: write to file
			paused = false;
		} else {
			pauseTimes = appendToArray(pauseTimes, System.currentTimeMillis());
			// TODO: write to file
			paused = true;
		}
		opanel.update(recording, paused);
	}

	private void writeToFile(String line) {
		try {
			file.write(line + "\n");
		} catch (IOException e) {
			System.out.println("Error occurred: " + e.getMessage());
		}
	}


	// helper method to append an element to an array
	public static long[] appendToArray(long[] arr, long elem) {
		long[] newArr = new long[arr.length + 1];
		System.arraycopy(arr, 0, newArr, 0, arr.length);
		newArr[arr.length] = elem;
		return newArr;
	}

	@Provides
	OstravaConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(OstravaConfig.class);
	}
}
