package leaderboardenabler;

import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

/**
 * Mod entry point:
 *  - initialize(): loads the SpireConfig settings and subscribes to BaseMod.
 *  - receivePostInitialize(): registers the in-game config panel once libGDX / fonts are ready.
 *
 * Settings (stored in the game's saves/ directory, editable in-game via the Mods menu):
 *  - bypassIsModded: allow "modded runs" to upload to the leaderboard (default true)
 *  - bypassSeedSet : allow "seeded runs" to upload to the leaderboard (default false)
 */
@SpireInitializer
public class LeaderboardEnabler implements PostInitializeSubscriber {

    private static final String MOD_ID = "LeaderboardEnabler";
    private static final String CONFIG_FILE = "config";

    private static SpireConfig config;

    public LeaderboardEnabler() {
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        try {
            config = new SpireConfig(MOD_ID, CONFIG_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new LeaderboardEnabler();
    }

    @Override
    public void receivePostInitialize() {
        setupConfigPanel();
    }

    public static boolean bypassIsModded() {
        return getBool("bypassIsModded", true);
    }

    public static boolean bypassSeedSet() {
        return getBool("bypassSeedSet", false);
    }

    private static boolean getBool(String key, boolean defaultValue) {
        if (config == null) {
            return defaultValue;
        }
        if (!config.has(key)) {
            config.setBool(key, defaultValue);
            saveConfig();
        }
        return config.getBool(key);
    }

    private static void saveConfig() {
        try {
            if (config != null) {
                config.save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setupConfigPanel() {
        ModPanel panel = new ModPanel();
        float x = 350.0f;
        float y = 750.0f;

        ModLabeledToggleButton moddedToggle = new ModLabeledToggleButton(
                "Allow modded runs to upload", x, y,
                Settings.CREAM_COLOR, FontHelper.charDescFont,
                bypassIsModded(), panel,
                label -> {},
                button -> {
                    config.setBool("bypassIsModded", button.enabled);
                    saveConfig();
                }
        );
        panel.addUIElement(moddedToggle);

        ModLabeledToggleButton seedToggle = new ModLabeledToggleButton(
                "Allow seeded runs to upload", x, y - 50.0f,
                Settings.CREAM_COLOR, FontHelper.charDescFont,
                bypassSeedSet(), panel,
                label -> {},
                button -> {
                    config.setBool("bypassSeedSet", button.enabled);
                    saveConfig();
                }
        );
        panel.addUIElement(seedToggle);

        Texture badgeTexture = new Texture("leaderboardenabler/badge.png");
        BaseMod.registerModBadge(badgeTexture, MOD_ID, "Connor",
                "Enables leaderboard uploads with mods.", panel);
    }
}
