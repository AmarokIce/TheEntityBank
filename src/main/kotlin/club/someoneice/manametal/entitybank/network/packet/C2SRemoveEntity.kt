package club.someoneice.manametal.entitybank.network.packet

import club.someoneice.manametal.entitybank.core.BankData
import club.someoneice.manametal.entitybank.core.EntityBank
import club.someoneice.manametal.entitybank.core.ThreadDataHandler
import club.someoneice.manametal.entitybank.network.IPacket
import club.someoneice.manametal.entitybank.network.NetworkHandler
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import java.util.*

data class C2SRemoveEntity(
  val uuid: UUID,
  val shouldGivePlayerBottle: Boolean = false,
  val giftToPlayer: String = ""
): IPacket {
  override fun pack(buff: PacketBuffer) {
    buff.writeStringToBuffer(uuid.toString())
    buff.writeBoolean(shouldGivePlayerBottle)
    buff.writeStringToBuffer(giftToPlayer)
  }

  override fun handle(buff: PacketBuffer, player: EntityPlayerMP?) {
    player ?: return

    val uuid = UUID.fromString(buff.readStringFromBuffer(36))
    val shouldGivePlayerBottle = buff.readBoolean()
    val gift = buff.readStringFromBuffer(96)
    ThreadDataHandler.TASKS.add { handle(player, uuid, shouldGivePlayerBottle, gift) }
  }

  fun handle(player: EntityPlayerMP, uuid: UUID, bottle: Boolean, giftTo: String) {
    val collection = BankData.getEntities(player.commandSenderName)
    if (collection.isEmpty()) {
      return
    }

    val entity = collection.firstOrNull {
      if (!it.hasKey("UUIDMost", 4) || !it.hasKey("UUIDLeast", 4)) {
        return@firstOrNull false
      }

      val uid = UUID(it.getLong("UUIDMost"), it.getLong("UUIDLeast"))
      return@firstOrNull uuid == uid
    }

    if (Objects.isNull(entity)) {
      return
    }

    collection.remove(entity!!)
    BankData.setEntities(player.commandSenderName, collection)

    if (giftTo.isNotEmpty()) {
      val entityPlayer = player.serverForPlayer.playerEntities
        .filterIsInstance<EntityPlayerMP>()
        .first { it.commandSenderName == giftTo }

      if (entity.hasKey("OwnerUUID", 8)) {
        entity.setString("OwnerUUID", entityPlayer.uniqueID.toString())
      }

      val list: MutableList<NBTTagCompound> = BankData.getEntities(giftTo)
      list.add(entity)
      BankData.setEntities(giftTo, list)

      NetworkHandler.sendPacket(S2CNotice, entityPlayer)
      return
    }

    if (!bottle) {
      return
    }

    val stack = ItemStack(EntityBank.SOULBOTTLE.get())
    stack.tagCompound = NBTTagCompound()
    stack.tagCompound.setTag("entity", entity)
    if (!player.inventory.addItemStackToInventory(stack)) {
      val entityItem = EntityItem(player.worldObj,
        player.posX, player.posY + 0.5, player.posZ, stack)
      player.worldObj.spawnEntityInWorld(entityItem)
    }
  }

  companion object {
    val INSTANCE = C2SRemoveEntity(UUID.fromString("00000000-0000-0000-0000-000000000000"))
  }
}
