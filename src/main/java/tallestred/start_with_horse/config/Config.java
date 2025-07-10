package tallestred.start_with_horse.config;

import com.google.common.collect.ImmutableList;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;
    public static final CommonConfig INSTANCE;

    static {
        Pair<CommonConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(CommonConfig::new);
        SPEC = pair.getRight();
        INSTANCE = pair.getLeft();
    }

    public static class CommonConfig {
        public final ModConfigSpec.ConfigValue<List<? extends String>> dyeableItemsForPlayerToAlsoSpawnWith;
        public final ModConfigSpec.ConfigValue<List<? extends String>> dyeableItemsForPlayerToEquip;
        public final ModConfigSpec.ConfigValue<List<? extends String>> nonDyedItems;
        public final ModConfigSpec.BooleanValue randomDye;
        public final ModConfigSpec.IntValue dyeColor;

        public CommonConfig(ModConfigSpec.Builder builder) {
            randomDye = builder.define("Random dye for leather goods?", true);
            dyeColor = builder.defineInRange("Convert color's hex to dec via online converter", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            dyeableItemsForPlayerToEquip = builder.defineListAllowEmpty("List of dyeable items for the player to equip", ImmutableList.of("minecraft:leather_helmet", "minecraft:leather_chestplate", "minecraft:leather_leggings", "minecraft:leather_boots"), () -> "", obj -> true);
            dyeableItemsForPlayerToAlsoSpawnWith = builder.defineListAllowEmpty("List of dyeable items for the player to spawn with", new ArrayList<>(), () -> "", obj -> true);
            nonDyedItems = builder.defineListAllowEmpty("List of items for the player to spawn with", new ArrayList<>(), () -> "", obj -> true);
        }
    }
}
