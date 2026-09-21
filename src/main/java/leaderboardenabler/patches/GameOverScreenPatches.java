package leaderboardenabler.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.GameOverScreen;
import leaderboardenabler.LeaderboardEnabler;

/**
 * The original GameOverScreen.canUploadLeaderboards() check is:
 *   return !Settings.isModded && !Settings.isTrial && !Settings.seedSet;
 *
 * This patch hands the isModded / seedSet checks over to the in-game config:
 *   - isTrial (demo version) is always enforced and cannot be bypassed.
 *   - Whether isModded / seedSet are bypassed depends on the toggles in the Mods menu.
 */
public class GameOverScreenPatches {

    @SpirePatch2(clz = GameOverScreen.class, method = "canUploadLeaderboards")
    public static class CanUploadLeaderboardsPatch {

        @SpirePrefixPatch
        public static SpireReturn<Boolean> prefix(GameOverScreen __instance) {
            boolean moddedOk = !Settings.isModded || LeaderboardEnabler.bypassIsModded();
            boolean trialOk = !Settings.isTrial;
            boolean seedOk = !Settings.seedSet || LeaderboardEnabler.bypassSeedSet();
            return SpireReturn.Return(moddedOk && trialOk && seedOk);
        }
    }
}
