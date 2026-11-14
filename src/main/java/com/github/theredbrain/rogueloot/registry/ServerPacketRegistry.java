package com.github.theredbrain.rogueloot.registry;

import com.github.theredbrain.rogueloot.network.packet.ApplyRogueLootSelectionPacket;
import com.github.theredbrain.rogueloot.network.packet.ApplyRogueLootSelectionPacketReceiver;
import com.github.theredbrain.rogueloot.network.packet.BanishRogueLootEntryPacket;
import com.github.theredbrain.rogueloot.network.packet.BanishRogueLootEntryPacketReceiver;
import com.github.theredbrain.rogueloot.network.packet.UpdateInteractiveRogueLootBlockPacket;
import com.github.theredbrain.rogueloot.network.packet.UpdateInteractiveRogueLootBlockPacketReceiver;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerPacketRegistry {

	public static void init() {

		PayloadTypeRegistry.serverboundPlay().register(ApplyRogueLootSelectionPacket.PACKET_ID, ApplyRogueLootSelectionPacket.PACKET_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(ApplyRogueLootSelectionPacket.PACKET_ID, new ApplyRogueLootSelectionPacketReceiver());

		PayloadTypeRegistry.serverboundPlay().register(BanishRogueLootEntryPacket.PACKET_ID, BanishRogueLootEntryPacket.PACKET_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(BanishRogueLootEntryPacket.PACKET_ID, new BanishRogueLootEntryPacketReceiver());

		PayloadTypeRegistry.serverboundPlay().register(UpdateInteractiveRogueLootBlockPacket.PACKET_ID, UpdateInteractiveRogueLootBlockPacket.PACKET_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(UpdateInteractiveRogueLootBlockPacket.PACKET_ID, new UpdateInteractiveRogueLootBlockPacketReceiver());

	}
}
