package com.github.theredbrain.rogueloot.network.packet;

import com.github.theredbrain.rogueloot.RogueLoot;
import com.github.theredbrain.rogueloot.data.DataAttachmentHelper;
import com.github.theredbrain.rogueloot.data.RogueLootTableEntry;
import com.github.theredbrain.rogueloot.data.RogueLootTableHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.List;
import java.util.Optional;

public class ApplyRogueLootSelectionPacketReceiver implements ServerPlayNetworking.PlayPayloadHandler<ApplyRogueLootSelectionPacket> {

	@Override
	public void receive(ApplyRogueLootSelectionPacket payload, ServerPlayNetworking.Context context) {

		ServerPlayer serverPlayer = context.player();
		ServerLevel serverLevel = serverPlayer.level();

		List<String> chosenRogueLootEntries = payload.chosenRogueLootEntries();
		Identifier localRogueLootTableIdentifier = payload.localRogueLootTableIdentifier();

		DataAttachmentHelper.removeGeneratedRogueLoot(serverLevel, localRogueLootTableIdentifier, serverPlayer);

		for (String entry : chosenRogueLootEntries) {
			Identifier identifier = Identifier.tryParse(entry);
			if (!entry.isEmpty() && identifier != null) {
				RogueLootTableEntry rogueLootTableEntry = RogueLootTableHelper.getRogueLootTableEntry(serverLevel.registryAccess(), identifier);
				if (rogueLootTableEntry != null) {
					for (RogueLootTableEntry.RogueLootTableEntryComponent component : rogueLootTableEntry.rogueLootTableEntryComponents()) {
						if (component.type() == RogueLootTableEntry.RogueLootTableEntryComponent.Type.ITEM_STACK) {
							RogueLoot.info("offer item stack");
							Optional<ItemStackTemplate> optionalItemStackTemplate = component.itemStackTemplate();
							if (optionalItemStackTemplate.isPresent()) {
								serverPlayer.getInventory().placeItemBackInInventory(optionalItemStackTemplate.get().create());
							}
						} else if (component.type() == RogueLootTableEntry.RogueLootTableEntryComponent.Type.ADVANCEMENT) {
							RogueLoot.info("offer advancement");
						} else if (component.type() == RogueLootTableEntry.RogueLootTableEntryComponent.Type.LOOT_TABLE) {
							RogueLoot.info("offer loot table");
						} else if (component.type() == RogueLootTableEntry.RogueLootTableEntryComponent.Type.MOB_EFFECT_INSTANCE) {
							RogueLoot.info("offer mob effect");
							Optional<MobEffectInstance> optionalMobEffectInstance = component.mobEffectInstance();
							if (optionalMobEffectInstance.isPresent()) {
								serverPlayer.addEffect(new MobEffectInstance(optionalMobEffectInstance.get()));
							}
						}
					}
				}
			}
		}
	}
}
