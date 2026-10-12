package club.someoneice.manametal.entitybank.core

import cpw.mods.fml.common.Loader
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import project.studio.manametalmod.entity.nbt.ManaMetalModRoot
import tw.pearki.mcmod.muya.common.entity.EntityNBTHelper
import java.io.File

object BankConfig {
  val CONFIG = File(Loader.instance().configDir, "entitybank.ini")
  var MONEY_NEEDED = 500000L
  var BANK_LENGTH = 512

  init {
    run {
      if (!CONFIG.exists() || !CONFIG.isFile) {
        createFile()
        return@run
      }

      val lines = CONFIG.readLines()
      MONEY_NEEDED = lines.firstOrNull { it.startsWith("MoneyNeeded=") }?.split("=")[1]?.toLong() ?: MONEY_NEEDED
      BANK_LENGTH = lines.firstOrNull { it.startsWith("BankLength=") }?.split("=")[1]?.toInt() ?: BANK_LENGTH
    }
  }

  fun checkPlayerMoney(player: EntityPlayer, test: Boolean): Boolean {
    val isCreative = if (player.worldObj.isRemote) {
      Minecraft.getMinecraft().playerController.isInCreativeMode
    } else {
      (player as EntityPlayerMP).theItemInWorldManager.isCreative
    }

    if (isCreative) {
      return true
    }

    val root = EntityNBTHelper.GetEntityNBT(player, ManaMetalModRoot::class.java)
    if (root.money.money < MONEY_NEEDED) {
      return false
    }

    if (!test) {
      root.money.addMoney(-MONEY_NEEDED)
    }

    return true
  }

  private fun createFile() {
    CONFIG.createNewFile()
    val sb = StringBuilder()
      .append("MoneyNeeded=$MONEY_NEEDED")
      .append("\n")
      .append("BankLength=$BANK_LENGTH")
    CONFIG.writeText(sb.toString())
  }
}
