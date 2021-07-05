package me.hypherionmc.storagedrawers.config;

import me.hypherionmc.storagedrawers.StorageDrawers;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

import java.util.ArrayList;
import java.util.List;

@Config(name = StorageDrawers.MOD_ID)
public final class CommonConfig implements ConfigData
{
    public static final General GENERAL = new General();
    public static final Upgrades UPGRADES = new Upgrades();
    public static final Integration INTEGRATION = new Integration();

    private static boolean loaded = false;
    private static List<Runnable> loadActions = new ArrayList<>();

    public static void setLoaded() {
        if (!loaded)
            loadActions.forEach(Runnable::run);
        loaded = true;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static void onLoad(Runnable action) {
        if (loaded)
            action.run();
        else
            loadActions.add(action);
    }

    public static class General {
        public final int baseStackStorage;
        public final boolean enableUI;
        public final boolean enableSidedInput;
        public final boolean enableSidedOutput;
        public final boolean enableItemConversion;
        public final boolean debugTrace;
        public final boolean enableExtraCompactingRules;
        public final int controllerRange;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> compRules;

        public General(ForgeConfigSpec.Builder builder) {
            builder.push("General");
            List<String> test = new ArrayList<>();
            test.add("minecraft:clay, minecraft:clay_ball, 4");

            baseStackStorage = builder
                .comment("The number of item stacks held in a basic unit of storage.",
                    "1x1 drawers hold 8 units, 1x2 drawers hold 4 units, 2x2 drawers hold 2 units.",
                    "Half-depth drawers hold half those amounts.")
                .define("baseStackStorage", 4);
            controllerRange = builder
                .defineInRange("controllerRange", 12, 1, 50);
            enableUI = builder
                .define("enableUI", true);
            enableSidedInput = builder
                .define("enableSidedInput", true);
            enableSidedOutput = builder
                .define("enableSidedOutput", true);
            enableItemConversion = builder
                .define("enableItemConversion", true);
            enableExtraCompactingRules = builder
                .define("enableExtraCompactingRules", true);
            debugTrace = builder
                .define("debugTrace", false);
            compRules = builder
                .comment("List of rules in format \"domain:item1, domain:item2, n\".",
                    "Causes a compacting drawer convert n of item1 into 1 of item2.")
                .defineList("compactingRules", test, obj -> CompTierRegistry.validateRuleSyntax((String)obj));

            builder.pop();
        }

        /*cache.compRules = config.getStringList("compactingRules", sectionRegistries.getQualifiedName(), new String[] { "minecraft:clay, minecraft:clay_ball, 4" }, "Items should be in form domain:item or domain:item:meta.", null, LANG_PREFIX + "registries.compRules");
        if (StorageDrawers.compRegistry != null) {
            for (String rule : cache.compRules)
                StorageDrawers.compRegistry.register(rule);
        }*/

        public int getBaseStackStorage() {
            if (!isLoaded())
                return 1;

            return baseStackStorage.get();
        }
    }

    public static class Integration {
        public final boolean wailaStackRemainder;

        public Integration (ForgeConfigSpec.Builder builder) {
            builder.push("Integration");

            wailaStackRemainder = builder
                .define("wailaStackRemainder", true);

            builder.pop();
        }
    }

    public static class Upgrades {
        public final int level1Mult;
        public final int level2Mult;
        public final int level3Mult;
        public final int level4Mult;
        public final int level5Mult;

        public Upgrades (ForgeConfigSpec.Builder builder) {
            builder.push("StorageUpgrades");
            builder.comment("Storage upgrades multiply storage capacity by the given amount.",
                "When multiple storage upgrades are used together, their multipliers are added before being applied.");

            level1Mult = builder
                .define("level1Mult", 2);
            level2Mult = builder
                .define("level2Mult", 4);
            level3Mult = builder
                .define("level3Mult", 8);
            level4Mult = builder
                .define("level4Mult", 16);
            level5Mult = builder
                .define("level5Mult", 32);

            builder.pop();
        }

        public int getLevelMult(int level) {
            if (!isLoaded())
                return 1;

            switch (level) {
                case 1: return level1Mult.get();
                case 2: return level2Mult.get();
                case 3: return level3Mult.get();
                case 4: return level4Mult.get();
                case 5: return level5Mult.get();
                default: return 1;
            }
        }
    }
}
