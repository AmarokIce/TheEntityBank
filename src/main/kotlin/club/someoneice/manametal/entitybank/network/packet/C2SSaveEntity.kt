package club.someoneice.manametal.entitybank.network.packet

import alexsocol.asjlib.set
import club.someoneice.manametal.entitybank.BankConfig
import club.someoneice.manametal.entitybank.EntityBank
import club.someoneice.manametal.entitybank.ThreadDataHandler
import club.someoneice.manametal.entitybank.network.IPacket
import com.google.common.collect.Lists
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import project.studio.manametalmod.entity.nbt.ManaMetalModRoot
import project.studio.manametalmod.items.ItemSoulBottle
import tw.pearki.mcmod.muya.common.entity.EntityNBTHelper

data class C2SSaveEntity(
  val entity: NBTTagCompound? = null,
  val player: String = ""
): IPacket {
  override fun pack(buff: PacketBuffer) {
    buff.writeNBTTagCompoundToBuffer(entity)
    buff.writeStringToBuffer(player)
  }

  override fun handle(buff: PacketBuffer, player: EntityPlayerMP?) {
    player ?: return
    val entity: NBTTagCompound? = buff.readNBTTagCompoundFromBuffer()
    val playerName = buff.readStringFromBuffer(128).ifEmpty(player::getDisplayName)
    ThreadDataHandler.TASKS.add { handle(player, playerName, entity) }
  }

  private fun handle(player: EntityPlayerMP, playerName: String, entity: NBTTagCompound?) {
    val entityNBT = entity ?: getEntityFromPlayerHand(player) ?: return

    if (playerName.isEmpty()) {
      val root = EntityNBTHelper.GetEntityNBT(player, ManaMetalModRoot::class.java)
      if (root.money.money < BankConfig.MONEY_NEEDED) {
        return
      }
      root.money.addMoney(-BankConfig.MONEY_NEEDED)
    }

    val db = EntityBank.createDB()
    val list: MutableList<NBTTagCompound> = if (db.exists(playerName)) {
      db.get(playerName, List::class.java).filterIsInstance<NBTTagCompound>().toMutableList()
    } else {
      Lists.newArrayList()
    }

    list.add(entityNBT)
    db.set(playerName, list as List<*>)
    db.close()
  }

  private fun getEntityFromPlayerHand(player: EntityPlayerMP): NBTTagCompound? {
    val itemInHand = player.heldItem
    if (itemInHand.item !is ItemSoulBottle || itemInHand.hasTagCompound()) {
      return null
    }

    if (!itemInHand.stackTagCompound.hasKey("entity", 10)) {
      return null
    }

    val nbt = itemInHand.tagCompound.getCompoundTag("entity")
    player.inventory[player.inventory.currentItem] = null
    return null
  }

  companion object {
    val INSTANCE = C2SSaveEntity()
  }
}
