package com.github.theredbrain.rogueloot.entity.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class PlayerHelper {

	/**
	 * @return Whether the cost has been paid
	 */
	public static boolean payItemCost(final ServerPlayer serverPlayer, final ItemStack costStack) {

		// create inventory copy
		int playerInventorySize = serverPlayer.getInventory().getContainerSize();
		Container playerInventoryCopy = new SimpleContainer(playerInventorySize);
		ItemStack itemStack;
		for (int k = 0; k < playerInventorySize; k++) {
			playerInventoryCopy.setItem(k, serverPlayer.getInventory().getItem(k).copy());
		}

		// check for itemCosts
		int itemCount = costStack.getCount();
		for (int j = 0; j < playerInventorySize; j++) {

			if (ItemStack.isSameItemSameComponents(playerInventoryCopy.getItem(j), costStack)) {
				itemStack = playerInventoryCopy.getItem(j).copy();
				int stackCount = itemStack.getCount();

				if (stackCount >= itemCount) {
					itemStack.setCount(stackCount - itemCount);
					playerInventoryCopy.setItem(j, itemStack);
					itemCount = 0;
					break;
				} else {
					playerInventoryCopy.setItem(j, ItemStack.EMPTY);
					itemCount = itemCount - stackCount;
				}
			}
		}
		if (itemCount > 0) {
			return false;
		}

		// apply item cost
		int ingredientCount = costStack.getCount();
		for (int j = 0; j < playerInventorySize; j++) {

			if (ItemStack.isSameItemSameComponents(serverPlayer.getInventory().getItem(j), costStack)) {
				itemStack = serverPlayer.getInventory().getItem(j).copy();
				int stackCount = itemStack.getCount();

				if (stackCount >= ingredientCount) {
					itemStack.setCount(stackCount - ingredientCount);
					serverPlayer.getInventory().setItem(j, itemStack);
					ingredientCount = 0;
					break;
				} else {
					serverPlayer.getInventory().setItem(j, ItemStack.EMPTY);
					ingredientCount = ingredientCount - stackCount;
				}
			}
		}
		return ingredientCount <= 0;
	}

	/**
	 * @return How many times the given itemStack is inside the serverPlayer's inventory
	 */
	public static int countAffordableTransactions(final ServerPlayer serverPlayer, final ItemStack costStack) {

		// player inventory copy
		int inventorySize = serverPlayer.getInventory().getContainerSize();
		Container playerInventoryCopy = new SimpleContainer(inventorySize);
		ItemStack itemStack;
		int j;
		for (int k = 0; k < inventorySize; k++) {
			playerInventoryCopy.setItem(k, serverPlayer.getInventory().getItem(k).copy());
		}

		int transactionAmount = 0;
		boolean continueCounting = true;
		while (continueCounting) {

			int itemCount = costStack.getCount();
			for (j = 0; j < inventorySize; j++) {
				itemStack = playerInventoryCopy.getItem(j).copy();
				if (ItemStack.isSameItemSameComponents(itemStack, costStack)) {
					int stackCount = itemStack.getCount();
					if (stackCount >= itemCount) {
						itemStack.setCount(stackCount - itemCount);
						playerInventoryCopy.setItem(j, itemStack);
						itemCount = 0;
						break;
					} else {
						playerInventoryCopy.setItem(j, ItemStack.EMPTY);
						itemCount = itemCount - stackCount;
					}
				}
			}
			if (itemCount > 0) {
				continueCounting = false;
			} else {
				transactionAmount += 1;
			}
		}
		return transactionAmount;
	}
}
