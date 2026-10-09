package com.example.evilvillagers;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(EvilVillagersMod.MOD_ID)
public class EvilVillagersMod {
    public static final String MOD_ID = "evilvillagers";

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<EvilVillagerEntity>> EVIL_VILLAGER =
            ENTITIES.register("evil_villager", () -> EntityType.Builder
                    .of(EvilVillagerEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10)
                    .build("evil_villager"));

    public EvilVillagersMod(IEventBus modBus) {
        ENTITIES.register(modBus);
        modBus.addListener(this::onAttributes);
        NeoForge.EVENT_BUS.addListener(this::onCommands);
    }

    private void onAttributes(EntityAttributeCreationEvent event) {
        event.put(EVIL_VILLAGER.get(), EvilVillagerEntity.createAttributes().build());
    }

    private void onCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("evilvillager")
                .requires(s -> s.hasPermission(2))
                .executes(c -> spawn(c.getSource(), -1))
                .then(Commands.argument("variant", IntegerArgumentType.integer(1, EvilVillagerEntity.VARIANT_COUNT))
                        .executes(c -> spawn(c.getSource(),
                                IntegerArgumentType.getInteger(c, "variant") - 1))));
    }

    private int spawn(CommandSourceStack src, int variant) {
        ServerLevel level = src.getLevel();
        EvilVillagerEntity e = EVIL_VILLAGER.get().create(level);
        if (e == null) return 0;
        Vec3 p = src.getPosition();
        e.moveTo(p.x, p.y, p.z, 0.0F, 0.0F);
        e.setVariant(variant < 0 ? level.random.nextInt(EvilVillagerEntity.VARIANT_COUNT) : variant);
        e.setPersistenceRequired();
        level.addFreshEntity(e);
        src.sendSuccess(() -> Component.literal("Spawned " + EvilVillagerEntity.NAMES[e.getVariant()]), true);
        return 1;
    }
}
