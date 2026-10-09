package com.example.evilvillagers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class EvilVillagerRenderer extends MobRenderer<EvilVillagerEntity, VillagerModel<EvilVillagerEntity>> {
    private static ResourceLocation tex(String path) {
        return ResourceLocation.withDefaultNamespace("textures/entity/villager/" + path + ".png");
    }

    private static final ResourceLocation[] BASE = {
            tex("villager"), tex("villager"), tex("villager"), tex("villager"),
            tex("type/desert"), tex("type/savanna"), tex("type/swamp")
    };
    private static final ResourceLocation[] OVERLAY = {
            null, tex("profession/armorer"), tex("profession/weaponsmith"), tex("profession/cleric"),
            null, null, tex("profession/butcher")
    };

    public EvilVillagerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new VillagerModel<>(ctx.bakeLayer(ModelLayers.VILLAGER)), 0.5F);
        this.addLayer(new Overlay(this));
    }

    @Override
    public ResourceLocation getTextureLocation(EvilVillagerEntity e) {
        return BASE[e.getVariant()];
    }

    private static class Overlay extends RenderLayer<EvilVillagerEntity, VillagerModel<EvilVillagerEntity>> {
        Overlay(RenderLayerParent<EvilVillagerEntity, VillagerModel<EvilVillagerEntity>> parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack pose, MultiBufferSource buf, int light, EvilVillagerEntity e,
                           float limbSwing, float limbSwingAmount, float partialTick,
                           float ageInTicks, float netHeadYaw, float headPitch) {
            ResourceLocation overlay = OVERLAY[e.getVariant()];
            if (overlay != null && !e.isInvisible()) {
                renderColoredCutoutModel(getParentModel(), overlay, pose, buf, light, e, -1);
            }
        }
    }
}
