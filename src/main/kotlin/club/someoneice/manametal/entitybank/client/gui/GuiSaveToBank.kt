package club.someoneice.manametal.entitybank.client.gui

import club.someoneice.manametal.entitybank.client.StyleBackground
import club.someoneice.manametal.entitybank.core.BankConfig
import club.someoneice.manametal.entitybank.network.NetworkHandler
import club.someoneice.manametal.entitybank.network.packet.C2SSaveEntity
import club.someoneice.wolftail.api.ITooltip
import club.someoneice.wolftail.api.style.IStyleUI
import club.someoneice.wolftail.ui.core.WScreen
import club.someoneice.wolftail.ui.widget.WButton
import club.someoneice.wolftail.ui.widget.WString
import club.someoneice.wolftail.util.UIPos
import com.google.common.collect.ImmutableList
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.resources.I18n
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.ChatComponentTranslation
import net.minecraft.util.IChatComponent
import project.studio.manametalmod.items.ItemSoulBottle
import java.util.*

object GuiSaveToBank: WScreen(256, 256) {
  val BUTTON_YES: WButton = object: WButton(
    I18n.format("entitybank.savebank.yes"),
    UIPos(40, 144, 80, 16),
      StyleBackground,
      clicked = {
        NetworkHandler.sendPacket(C2SSaveEntity())
        mc.displayGuiScreen(null)
      }
  ), ITooltip {
    override fun canBeClick(): Boolean {
      val player = mc.thePlayer

      val itemInHand = player.heldItem
      val isHeldSoulBottle = Objects.nonNull(itemInHand)
        && itemInHand.item is ItemSoulBottle
        && itemInHand.hasTagCompound()
        && itemInHand.tagCompound.hasKey("entity", 10)
      return isHeldSoulBottle && BankConfig.checkPlayerMoney(player, true)
    }

    override fun getData(): ImmutableList<IChatComponent> {
      val text = if (this.canBeClick()) "check" else "none"
      return ImmutableList.of(ChatComponentTranslation("entitybank.savebank.${text}"))
    }
  }

  val BUTTON_NO: WButton = WButton(
    I18n.format("entitybank.savebank.no"),
    UIPos(150, 144, 80, 16),
      StyleBackground
  ) {
    mc.displayGuiScreen(null)
  }

  override fun initWidgets() {
    val text = I18n.format("entitybank.savebank.info", BankConfig.MONEY_NEEDED)
    this.addWidget(WString(text, 128 - fontRendererObj.getStringWidth(text) / 2, 103))
    this.addWidget(BUTTON_YES)
    this.addWidget(BUTTON_NO)
  }

  override fun getStyle(): IStyleUI = IStyleUI.INSTANCE

  override fun drawBackground(sign: Int) {
    this.drawWorldBackground(sign)
  }

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    val player = mc.thePlayer

    val itemInHand = player.heldItem
    val isHeldSoulBottle = Objects.nonNull(itemInHand)
      && itemInHand.item is ItemSoulBottle
      && itemInHand.hasTagCompound()
      && itemInHand.tagCompound.hasKey("entity", 10)

    if (!isHeldSoulBottle) {
      return
    }

    val entity = ItemSoulBottle.createEntityFromNBT(
      itemInHand.tagCompound.getCompoundTag("entity"),
      mc.theWorld, 0, 0, 0
    ) as? EntityLivingBase ?: return

    val x: Int = if (this.w == -1) 0 else (this.width - this.w) / 2
    val y: Int = if (this.h == -1) 0 else (this.height - this.h) / 2

    GuiInventory.func_147046_a(
      x + 128, y + 80, 30,
      x + 128 - mouseX.toFloat(), y + 30 - mouseY.toFloat(),
      entity)
  }
}
