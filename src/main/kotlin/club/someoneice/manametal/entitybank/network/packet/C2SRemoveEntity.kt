package club.someoneice.manametal.entitybank.network.packet

import club.someoneice.manametal.entitybank.EntityBank
import club.someoneice.manametal.entitybank.ThreadDataHandler
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
  val shouldGivePlayerBottle: Boolean = false
): IPacket {
  override fun pack(buff: PacketBuffer) {
    buff.writeStringToBuffer(uuid.toString())
    buff.writeBoolean(shouldGivePlayerBottle)
  }

  override fun handle(buff: PacketBuffer, player: EntityPlayerMP?) {
    player ?: return

    val uuid = UUID.fromString(buff.readStringFromBuffer(36))
    val shouldGivePlayerBottle = buff.readBoolean()
    ThreadDataHandler.TASKS.add { handle(player, uuid, shouldGivePlayerBottle) }
  }

  fun handle(player: EntityPlayerMP, uuid: UUID, bottle: Boolean) {
    val db = EntityBank.createDB()
    if (!db.exists(player.commandSenderName)) {
      NetworkHandler.sendPacket(S2CEntities.INSTANCE, player)
      return
    }

    val collection = db.get(player.commandSenderName, List::class.java).filterIsInstance<NBTTagCompound>().toMutableList()
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
      db.close()
      return
    }

    collection.remove(entity!!)
    if (!bottle) {
      db.set(player.commandSenderName, collection as List<*>)
      db.close()
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
    db.set(player.commandSenderName, collection as List<*>)
    db.close()
  }

  companion object {
    val INSTANCE = C2SRemoveEntity(UUID.fromString("00000000-0000-0000-0000-000000000000"))
  }
}
