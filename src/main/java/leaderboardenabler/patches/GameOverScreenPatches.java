package leaderboardenabler.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.GameOverScreen;
import leaderboardenabler.LeaderboardEnabler;

/**
 * 原版 GameOverScreen.canUploadLeaderboards() 的判定是：
 *   return !Settings.isModded && !Settings.isTrial && !Settings.seedSet;
 *
 * 本补丁把 isModded / seedSet 两项交给游戏内配置控制：
 *   - isTrial（试玩版）始终保留，不可绕过。
 *   - isModded / seedSet 是否绕过，取决于 Mods 菜单里的开关。
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
