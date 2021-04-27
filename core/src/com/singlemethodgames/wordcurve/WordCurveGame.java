package com.singlemethodgames.wordcurve;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.pay.PurchaseManager;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.singlemethodgames.wordcurve.actors.gamebar.GameBar;
import com.singlemethodgames.wordcurve.actors.gamebar.GameMode;
import com.singlemethodgames.wordcurve.screens.BaseScreen;
import com.singlemethodgames.wordcurve.screens.MainMenuScreen;
import com.singlemethodgames.wordcurve.screens.splash.DeveloperSplashScreen;
import com.singlemethodgames.wordcurve.screens.variants.Variant;
import com.singlemethodgames.wordcurve.services.GameServices;
import com.singlemethodgames.wordcurve.services.PlatformResolver;
import com.singlemethodgames.wordcurve.utils.Assets;
import com.singlemethodgames.wordcurve.utils.Constants;
import com.singlemethodgames.wordcurve.utils.NotificationService;
import com.singlemethodgames.wordcurve.utils.SecureView;
import com.singlemethodgames.wordcurve.utils.preferences.GamePreferences;
import com.singlemethodgames.wordcurve.utils.preferences.HighScores;
import com.singlemethodgames.wordcurve.utils.preferences.Statistics;
import com.singlemethodgames.wordcurve.utils.store.StoreManager;
import com.singlemethodgames.wordcurve.utils.tracking.Tracker;
import com.singlemethodgames.wordcurve.utils.wordlist.WordLookup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class WordCurveGame extends Game {
	public SpriteBatch batch;
	public WordLookup wordLookup;
	public HighScores highScores;
	public GamePreferences gamePreferences;
	public Statistics statistics;
	public OrthographicCamera camera;
	public Viewport viewport;
	public SecureView secureView;

	public GameServices gameServices;
	public PlatformResolver platformResolver;
	private Assets assets;
	public boolean ready = false;
	private long seed = System.currentTimeMillis();
	private Texture devLogo;
	public RandomXS128 random = new RandomXS128(seed);

	public final Json json = new Json();
	private DisableMultitouch disableMultitouch = new DisableMultitouch();
	private NotificationService notificationService = null;

	public final String deviceId;

	// IAP
	public PurchaseManager purchaseManager;
	public StoreManager storeManager;
	public MainMenuScreen mainMenuScreen = null;

	public static final String c = "yv6 ) y5*&^%";
	public static final String d = "$Y*@)um#)(m";

	public static final int WIDTH = 1080;
	public static final int HEIGHT = 1920;

	public WordCurveGame(GameServices gameServices, PlatformResolver platformResolver, SecureView secureView, String deviceId) {
		this.deviceId = deviceId;
		this.secureView = secureView;
	    this.gameServices = gameServices;
	    this.platformResolver = platformResolver;
    }

	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_ERROR);

		camera = new OrthographicCamera(WIDTH, HEIGHT);
		camera.setToOrtho(false, WIDTH, HEIGHT);

		viewport = new FitViewport(WIDTH, HEIGHT, camera);
		viewport.apply();

		Gdx.input.setCatchKey(Input.Keys.BACK, true);

		batch = new SpriteBatch();

		new Thread(new Runnable() {
			@Override
			public void run() {
				final WordLookup tempWordLookup = json.fromJson(WordLookup.class, Gdx.files.internal(Constants.JsonFiles.WORD_DICT));
				final HighScores tempHighScores = new HighScores();
				final GamePreferences tempGamePreferences = new GamePreferences();
				final Statistics tmpStatistics = new Statistics();

				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						wordLookup = tempWordLookup;
						highScores = tempHighScores;
						gamePreferences = tempGamePreferences;
						statistics = tmpStatistics;
						ready = true;
					}
				});
			}
		}).start();

		devLogo = new Texture(Gdx.files.internal(Constants.TextureRegions.SINGLE_METHOD_GAMES_LOGO + ".png"));

		assets = new Assets();
		assets.load();

		SaveState saveState = loadSaveState();
		if(saveState != null) {
            random.setSeed(saveState.getSeed());
        }

		this.setScreen(new DeveloperSplashScreen(this, saveState, devLogo));
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}

	public AssetManager getAssetManager() {
		return assets.manager;
	}

	public void instantiateNotificationService() {
		this.notificationService = new NotificationService(this);
	}

	public void notifyUser(String message) {
		if(this.notificationService != null) {
			this.notificationService.displayNotificationWithMessage(message);
		}
	}

	public static String checksum(String total) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(total.getBytes(StandardCharsets.UTF_8));
			total = String.valueOf(Base64Coder.encode(digest));
		} catch (NoSuchAlgorithmException ignored) {}

		return total;
	}

	public void saveGame(Variant variant, GameMode gameMode, GameBar.State gameState, Tracker tracker, boolean training) {
		SaveState saveState = new SaveState(seed, variant, gameMode, gameState, tracker, training);
		Preferences prefs = Gdx.app.getPreferences("save");
		String id = prefs.getString("a", "");
		if(id.isEmpty()) {
			id = UUID.randomUUID().toString();
			prefs.putString("a", Base64Coder.encodeString(id));
			prefs.flush();
		} else {
			id = Base64Coder.decodeString(id);
		}

		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(byteOutputStream);
			oos.writeObject(saveState);
			oos.close();
			byteOutputStream.close();

			String encoded = String.valueOf(Base64Coder.encode(byteOutputStream.toByteArray()));
			prefs.putString("s", encoded);
			prefs.putString("c", checksum(encoded + id));
			prefs.flush();

		} catch (IOException ignore) {
		    clearSave();
        }
	}

	public void clearSave() {
		Preferences prefs = Gdx.app.getPreferences("save");
		prefs.clear();
		prefs.flush();
	}

	private SaveState loadSaveState() {
		Preferences prefs = Gdx.app.getPreferences("save");
		String id = prefs.getString("a", "");
		if(id.isEmpty()) {
			id = UUID.randomUUID().toString();
			prefs.putString("a", Base64Coder.encodeString(id));
			prefs.flush();
		} else {
			id = Base64Coder.decodeString(id);
		}
		String base64EncodedString = prefs.getString("s", "");

		if(!base64EncodedString.isEmpty()) {
			String savedChecksum = prefs.getString("c", "");
			String generatedChecksum = checksum(base64EncodedString + id);

			if(savedChecksum.equals(generatedChecksum)) {
				try {
					ByteArrayInputStream bis = new ByteArrayInputStream(Base64Coder.decode(base64EncodedString));
					ObjectInputStream ois = new ObjectInputStream(bis);
					SaveState saveState = (SaveState) ois.readObject();
					ois.close();
					bis.close();

					return saveState;
				} catch (IOException | ClassNotFoundException ignore) {
                    clearSave();
                }
			}
		}
		return null;
	}

	public void newSeed() {
		seed = System.currentTimeMillis();
		random.setSeed(seed);
	}

	public void setScreen(BaseScreen screen) {
		super.setScreen(screen);

		if (screen.getInputProcessor() != null) {
			Gdx.input.setInputProcessor(
					new InputMultiplexer(
							disableMultitouch,
							screen.getInputProcessor()
					)
			);

			if(this.notificationService != null && this.notificationService.getNotificationTable() != null && (this.notificationService.getNotificationTable().hasActions() || this.notificationService.getNotificationTable().getColor().a > 0)) {
				((Stage)screen.getInputProcessor()).addActor(this.notificationService.getNotificationTable());
			}
		}
	}

	public long getSeed() {
		return seed;
	}

	@Override
	public void dispose () {
		purchaseManager.dispose();
		devLogo.dispose();
		batch.dispose();
		assets.dispose();
	}

	private class DisableMultitouch implements InputProcessor {
		@Override
		public boolean keyDown(int keycode) {
			return false;
		}

		@Override
		public boolean keyUp(int keycode) {
			return false;
		}

		@Override
		public boolean keyTyped(char character) {
			return false;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			return pointer > 0;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			return pointer > 0;
		}

		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {
			return pointer > 0;
		}

		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			return false;
		}

		@Override
		public boolean scrolled(int amount) {
			return false;
		}
	}
}
