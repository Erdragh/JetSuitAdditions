package com.github.erdragh.jet_suit_additions.items.armour;

import com.github.alexnijjar.ad_astra.AdAstra;
import com.github.alexnijjar.ad_astra.items.armour.JetSuit;
import com.github.alexnijjar.ad_astra.registry.ModItems;
import com.github.alexnijjar.ad_astra.util.ModKeyBindings;
import com.github.erdragh.jet_suit_additions.JetSuitAdditions;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class ImprovedJetSuit extends JetSuit {

    public static final int HOVER_SINK_SPEED = 1;
    public ImprovedJetSuit(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
        super(material, slot, settings);
    }

    @Override
    public void fly(PlayerEntity player, ItemStack stack) {
        // Don't fly if the Jet Suit is disabled
        if (!stack.getOrCreateNbt().getBoolean("toggle_on")) {
            stack.getOrCreateNbt().putBoolean("spawn_particles", false);
            return;
        }
        super.fly(player, stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (stack.isOf(ModItems.JET_SUIT)) {
            boolean turnedOn = stack.getOrCreateNbt().getBoolean("toggle_on");
            boolean hoverOn = stack.getOrCreateNbt().getBoolean("toggle_hover");
            Text turnedOnText = new TranslatableText(JetSuitAdditions.MODID + ".msg.jet_suit_toggle").append(new TranslatableText(JetSuitAdditions.MODID + ".msg.jet_suit_" + (turnedOn ? "on" : "off")).setStyle(Style.EMPTY.withBold(true).withColor(turnedOn ? Formatting.GREEN : Formatting.RED)));
            Text hoverOnText = new TranslatableText(JetSuitAdditions.MODID + ".msg.jet_suit_toggle_hover").append(new TranslatableText(JetSuitAdditions.MODID + ".msg.jet_suit_" + (hoverOn ? "on" : "off")).setStyle(Style.EMPTY.withBold(true).withColor(hoverOn ? Formatting.GREEN : Formatting.RED)));
            tooltip.add(turnedOnText);
            tooltip.add(hoverOnText);
        }
    }

    @Override
    public void hover(PlayerEntity player, ItemStack stack) {
        if (ModKeyBindings.jumpKeyDown(player)) {
            super.hover(player, stack);
        } else {
            double speed;
            if (player.isSneaking()) {
                speed = -AdAstra.CONFIG.spaceSuit.jetSuitUpwardsSpeed;
            } else {
                speed = -player.getVelocity().getY() / HOVER_SINK_SPEED;
            }
            if (!player.isOnGround()) {
                hover(player, stack, speed);
            } else {
                stack.getOrCreateNbt().putBoolean("spawn_particles", false);
            }
        }
    }

    private void hover(PlayerEntity player, ItemStack stack, double speed) {
        player.fallDistance /= 2;
        if (!player.isCreative() && !this.tryUseEnergy(stack, AdAstra.CONFIG.spaceSuit.jetSuitEnergyPerTick)) {
            this.setStoredEnergy(stack, 0);
        }
        isFallFlying = false;

        player.setVelocity(player.getVelocity().add(0.0, speed, 0.0));
        if (Math.abs(player.getVelocity().getY()) > Math.abs(speed)) {
            player.setVelocity(player.getVelocity().getX(), speed, player.getVelocity().getZ());
        }
    }
}
