package club.someoneice.manametal.entitybank.network.packet

import club.someoneice.manametal.entitybank.network.IPacket
import com.google.common.collect.Lists
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer

data class S2CEntities(
  val maxSize: Int,
  val index: Int,
  val entities: List<NBTTagCompound>
): IPacket {
  override fun pack(buff: PacketBuffer) {
    buff.writeByte(maxSize)
    buff.writeByte(index)
    buff.writeInt(entities.size)
    for (entity in entities) {
      buff.writeNBTTagCompoundToBuffer(entity)
    }
  }

  override fun handle(buff: PacketBuffer, player: EntityPlayerMP?) {
    val maxSize = buff.readByte().toInt()
    val index = buff.readByte().toInt()
    val size = buff.readInt()

    val list = Lists.newArrayList<NBTTagCompound>()
    for (i in 0 until size) {
      list.add(buff.readNBTTagCompoundFromBuffer())
    }

    PACK_RES.add(S2CEntities(maxSize, index, list))
  }

  companion object {
    val INSTANCE = S2CEntities(0, 0, listOf())

    val PACK_RES: ArrayList<S2CEntities> = Lists.newArrayListWithExpectedSize(10)
  }
}
