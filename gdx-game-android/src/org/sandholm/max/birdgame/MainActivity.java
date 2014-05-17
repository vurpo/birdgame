package org.sandholm.max.birdgame;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.*;

public class MainActivity extends AndroidApplication {
	private Preferences prefs;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = false;
		
		prefs = getPreferences("org.sandholm.max.birdgame.preferences");
        
        initialize(new BirdGame(prefs), cfg);
    }
}
