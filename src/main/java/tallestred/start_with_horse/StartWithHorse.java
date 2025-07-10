package tallestred.start_with_horse;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import tallestred.start_with_horse.config.Config;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.function.Supplier;

import static tallestred.start_with_horse.StartWithHorse.MODID;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(MODID)
@EventBusSubscriber(modid = MODID)
public class StartWithHorse {
    public static final String MODID = "spawn_with_horse";
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, StartWithHorse.MODID);
    public static final Supplier<AttachmentType<Boolean>> FIRST_JOINED = ATTACHMENT_TYPES.register(
            "first_joined", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build()
    );

    public StartWithHorse(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        ATTACHMENT_TYPES.register(modEventBus);
    }

    @SubscribeEvent
    public static void onPlayerFirstLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        Level level = player.level();
        if (!player.hasData(FIRST_JOINED.get())) {
            player.setData(FIRST_JOINED.get(), true);
            Horse horse = EntityType.HORSE.create(level);
            if (level instanceof ServerLevelAccessor)
                horse.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(player.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
            ItemStack horseArmor = new ItemStack(Items.LEATHER_HORSE_ARMOR);
            DyedItemColor color = new DyedItemColor(Config.INSTANCE.randomDye.get() ? level.random.nextInt() : Config.INSTANCE.dyeColor.get(), true);
            horseArmor.set(DataComponents.DYED_COLOR, color);
            for (String equipDyed : Config.INSTANCE.dyeableItemsForPlayerToEquip.get()) {
                ItemStack equipDyedStack = BuiltInRegistries.ITEM.get(ResourceLocation.parse(equipDyed)).getDefaultInstance();
                equipDyedStack.set(DataComponents.DYED_COLOR, color);
                player.setItemSlot(player.getEquipmentSlotForItem(equipDyedStack), equipDyedStack);
            }
            for (String itemDyed : Config.INSTANCE.dyeableItemsForPlayerToAlsoSpawnWith.get()) {
                ItemStack itemDyedStack = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemDyed)).getDefaultInstance();
                itemDyedStack.set(DataComponents.DYED_COLOR, color);
                player.getInventory().add(itemDyedStack);
            }
            for (String item : Config.INSTANCE.nonDyedItems.get()) {
                ItemStack itemStack = BuiltInRegistries.ITEM.get(ResourceLocation.parse(item)).getDefaultInstance();
                player.getInventory().add(itemStack);
            }
            horse.copyPosition(player);
            horse.setOwnerUUID(player.getUUID());
            horse.setTamed(true);
            player.startRiding(horse);
            horse.equipSaddle(new ItemStack(Items.SADDLE), SoundSource.NEUTRAL);
            horse.setBodyArmorItem(horseArmor);
            level.addFreshEntity(horse);
        }
    }
}
