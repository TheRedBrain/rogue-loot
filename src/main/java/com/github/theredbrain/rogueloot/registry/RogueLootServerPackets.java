package com.github.theredbrain.rogueloot.registry;

import com.github.theredbrain.rogueloot.network.packet.ApplyRogueLootSelectionPacket;
import com.github.theredbrain.rogueloot.network.packet.ApplyRogueLootSelectionPacketReceiver;
import com.github.theredbrain.rogueloot.network.packet.BanishRogueLootEntryPacket;
import com.github.theredbrain.rogueloot.network.packet.BanishRogueLootEntryPacketReceiver;
import com.github.theredbrain.rogueloot.network.packet.LockRogueLootEntryPacket;
import com.github.theredbrain.rogueloot.network.packet.LockRogueLootEntryPacketReceiver;
import com.github.theredbrain.rogueloot.network.packet.RerollRogueLootScreenPacket;
import com.github.theredbrain.rogueloot.network.packet.RerollRogueLootScreenPacketReceiver;
import com.github.theredbrain.rogueloot.network.packet.UpdateInteractiveRogueLootBlockPacket;
import com.github.theredbrain.rogueloot.network.packet.UpdateInteractiveRogueLootBlockPacketReceiver;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class RogueLootServerPackets {

	public static void init() {

		PayloadTypeRegistry.serverboundPlay().register(ApplyRogueLootSelectionPacket.PACKET_ID, ApplyRogueLootSelectionPacket.PACKET_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(ApplyRogueLootSelectionPacket.PACKET_ID, new ApplyRogueLootSelectionPacketReceiver());

		PayloadTypeRegistry.serverboundPlay().register(BanishRogueLootEntryPacket.PACKET_ID, BanishRogueLootEntryPacket.PACKET_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(BanishRogueLootEntryPacket.PACKET_ID, new BanishRogueLootEntryPacketReceiver());

		PayloadTypeRegistry.serverboundPlay().register(LockRogueLootEntryPacket.PACKET_ID, LockRogueLootEntryPacket.PACKET_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(LockRogueLootEntryPacket.PACKET_ID, new LockRogueLootEntryPacketReceiver());

		PayloadTypeRegistry.serverboundPlay().register(RerollRogueLootScreenPacket.PACKET_ID, RerollRogueLootScreenPacket.PACKET_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(RerollRogueLootScreenPacket.PACKET_ID, new RerollRogueLootScreenPacketReceiver());

		PayloadTypeRegistry.serverboundPlay().register(UpdateInteractiveRogueLootBlockPacket.PACKET_ID, UpdateInteractiveRogueLootBlockPacket.PACKET_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(UpdateInteractiveRogueLootBlockPacket.PACKET_ID, new UpdateInteractiveRogueLootBlockPacketReceiver());

	}
}
