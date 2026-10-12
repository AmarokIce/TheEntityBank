package club.someoneice.manametal.entitybank.network.packet

import alexsocol.asjlib.set
import club.someoneice.manametal.entitybank.core.BankConfig
import club.someoneice.manametal.entitybank.core.BankData
import club.someoneice.manametal.entitybank.core.ThreadDataHandler
import club.someoneice.manametal.entitybank.network.IPacket
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import project.studio.manametalmod.items.ItemSoulBottle

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
    val playerName = buff.readStringFromBuffer(96)
    ThreadDataHandler.TASKS.add { handle(player, playerName, entity) }
  }

  private fun handle(player: EntityPlayerMP, playerName: String, entity: NBTTagCompound?) {
    val entityNBT = entity ?: getEntityFromPlayerHand(player) ?: return

    if (playerName.isEmpty() && !BankConfig.checkPlayerMoney(player, false)) {
      return
    }

    val playerTarget = playerName.ifEmpty(player::getCommandSenderName)
    val list: MutableList<NBTTagCompound> = BankData.getEntities(playerTarget)
    list.add(entityNBT)
    BankData.setEntities(playerTarget, list)
  }

  private fun getEntityFromPlayerHand(player: EntityPlayerMP): NBTTagCompound? {
    val itemInHand = player.heldItem
    if (itemInHand.item !is ItemSoulBottle || !itemInHand.hasTagCompound()) {
      return null
    }

    if (!itemInHand.stackTagCompound.hasKey("entity", 10)) {
      return null
    }

    val nbt = itemInHand.tagCompound.getCompoundTag("entity")
    player.inventory[player.inventory.currentItem] = null
    return nbt
  }

  companion object {
    val INSTANCE = C2SSaveEntity()
  }
}
