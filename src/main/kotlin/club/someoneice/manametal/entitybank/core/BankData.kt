package club.someoneice.manametal.entitybank.core

import cpw.mods.fml.common.Loader
import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.server.MinecraftServer
import net.openhft.chronicle.map.ChronicleMap
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

object BankData {
  val DATA_BASE: ChronicleMap<CharSequence, ByteArray> = ChronicleMap
    .of(CharSequence::class.java, ByteArray::class.java)
    .name("entitybank")
    .entries(MinecraftServer.getServer().maxPlayers.toLong())
    .averageKeySize(96.0)
    .averageValueSize(2048.0 * BankConfig.BANK_LENGTH)
    .createPersistedTo(File(Loader.instance().configDir, "entitybank.dat"))

  fun setEntities(playerName: String, entities: List<NBTTagCompound>) {
    val list = NBTTagList()
    entities.forEach(list::appendTag)

    val outputStream = ByteArrayOutputStream()
    val comp = NBTTagCompound().apply {
      this.setTag("data", list)
    }

    CompressedStreamTools.writeCompressed(comp, outputStream)
    DATA_BASE[playerName] = outputStream.toByteArray()
    outputStream.close()
  }

  fun getEntities(playerName: String): MutableList<NBTTagCompound> {
    if (!DATA_BASE.containsKey(playerName)) {
      return arrayListOf()
    }

    val arr = DATA_BASE[playerName]
    val inputStream = ByteArrayInputStream(arr)
    val comp = CompressedStreamTools.readCompressed(inputStream)
    inputStream.close()

    val list = comp.getTag("data") as NBTTagList
    val entities = arrayListOf<NBTTagCompound>()

    for (i in 0 until list.tagCount()) {
      val entity = list.getCompoundTagAt(i)
      entities.add(entity)
    }

    return entities
  }
}
