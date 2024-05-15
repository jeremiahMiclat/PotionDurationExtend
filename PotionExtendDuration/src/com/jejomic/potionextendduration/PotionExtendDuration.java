package com.jejomic.potionextendduration;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.Collection;



public class PotionExtendDuration extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "PotionX Plugin Enabled");
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "PotionX Plugin Disabled");
    }


    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item.getType() == Material.POTION) {
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            PotionEffectType potionType = meta.getBasePotionType().getEffectType();
            int potionDuration = meta.getBasePotionType().getPotionEffects().getFirst().getDuration();

            boolean hasEffect = false;
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (effect.getType().equals(potionType)) {
                    int newDuration = effect.getDuration() + potionDuration;
                    player.addPotionEffect(new PotionEffect(potionType, newDuration, effect.getAmplifier(), effect.isAmbient(), effect.hasParticles(), effect.hasIcon()));

                    hasEffect = true;
                    break;
                }
            }

            if (!hasEffect) {
                player.addPotionEffect(new PotionEffect(potionType, potionDuration, 0));

            }
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {

        if (event.getPotion().getItem().getType() == Material.SPLASH_POTION) {
            Collection<LivingEntity> affectedEntities = event.getAffectedEntities();

            for (LivingEntity entity : affectedEntities) {

                for (PotionEffect effect : event.getPotion().getEffects()) {
                    PotionEffect existingEffect = entity.getPotionEffect(effect.getType());
                    if (existingEffect != null) {

                        int newDuration = existingEffect.getDuration() + effect.getDuration();

                        newDuration = Math.min(newDuration, Integer.MAX_VALUE);

                        entity.addPotionEffect(new PotionEffect(effect.getType(), newDuration, effect.getAmplifier()));
                    } else {

                        entity.addPotionEffect(effect);
                    }
                }
            }
        }
    }
}
