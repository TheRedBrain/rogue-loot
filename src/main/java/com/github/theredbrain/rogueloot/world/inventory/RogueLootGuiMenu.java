package com.github.theredbrain.rogueloot.world.inventory;

import com.github.theredbrain.rogueloot.data.GeneratedRogueLoot;
import com.github.theredbrain.rogueloot.registry.RogueLootMenuTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class RogueLootGuiMenu extends AbstractContainerMenu {
	private final GeneratedRogueLoot generatedRogueLoot;
	private final Identifier localRogueLootTableIdentifier;

	public RogueLootGuiMenu(int syncId, Inventory playerInventory, RogueLootGuiData data) {
		this(syncId, playerInventory, data.generatedRogueLoot(), data.localIdentifier());
	}

	public RogueLootGuiMenu(int syncId, Inventory playerInventory, GeneratedRogueLoot generatedRogueLoot, Identifier localRogueLootTableIdentifier) {
		super(RogueLootMenuTypes.ROGUE_LOOT_GUI_MENU_TYPE, syncId);
		this.generatedRogueLoot = generatedRogueLoot;
		this.localRogueLootTableIdentifier = localRogueLootTableIdentifier;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slotIndex) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	public GeneratedRogueLoot getGeneratedRogueLoot() {
		return this.generatedRogueLoot;
	}

	public Identifier getLocalRogueLootTableIdentifier() {
		return this.localRogueLootTableIdentifier;
	}

	public record RogueLootGuiData(
			GeneratedRogueLoot generatedRogueLoot,
			Identifier localIdentifier
	) {

		public static final StreamCodec<RegistryFriendlyByteBuf, RogueLootGuiData> STREAM_CODEC = StreamCodec.ofMember(RogueLootGuiData::write, RogueLootGuiData::new);

		public RogueLootGuiData(RegistryFriendlyByteBuf registryByteBuf) {
			this(
					GeneratedRogueLoot.STREAM_CODEC.decode(registryByteBuf),
					Identifier.STREAM_CODEC.decode(registryByteBuf)
			);
		}

		private void write(RegistryFriendlyByteBuf registryByteBuf) {
			GeneratedRogueLoot.STREAM_CODEC.encode(registryByteBuf, this.generatedRogueLoot);
			Identifier.STREAM_CODEC.encode(registryByteBuf, this.localIdentifier);
		}
	}

}
