package club.someoneice.manametal.entitybank.client.gui

import club.someoneice.manametal.entitybank.BankConfig
import club.someoneice.manametal.entitybank.client.StyleBackground
import club.someoneice.manametal.entitybank.network.NetworkHandler
import club.someoneice.manametal.entitybank.network.packet.C2SSaveEntity
import club.someoneice.wolftail.api.style.IStyleUI
import club.someoneice.wolftail.ui.core.WScreen
import club.someoneice.wolftail.ui.widget.WButton
import club.someoneice.wolftail.ui.widget.WString
import club.someoneice.wolftail.util.UIPos
import net.minecraft.client.resources.I18n
import project.studio.manametalmod.entity.nbt.ManaMetalModRoot
import project.studio.manametalmod.items.ItemSoulBottle
import tw.pearki.mcmod.muya.common.entity.EntityNBTHelper

object GuiSaveToBank: WScreen(256, 256) {
  val BUTTON_YES = object: WButton(
    I18n.format("entitybank.savebank.yes"),
    UIPos(40, 144, 80, 16),
      StyleBackground,
    { NetworkHandler.sendPacket(C2SSaveEntity()) }
  ) {
    override fun canBeClick(): Boolean {
      val player = mc.thePlayer

      val itemInHand = player.heldItem
      if (itemInHand.item !is ItemSoulBottle) {
        return false
      }

      val root = EntityNBTHelper.GetEntityNBT(player, ManaMetalModRoot::class.java)
      val flagReCheck = itemInHand.hasTagCompound() && itemInHand.stackTagCompound.hasKey("entity", 10)
      return root.money.money >= BankConfig.MONEY_NEEDED && flagReCheck
    }
  }

  val BUTTON_NO = WButton(
    I18n.format("entitybank.savebank.no"),
    UIPos(40, 144, 80, 16),
      StyleBackground
  ) {
    mc.displayGuiScreen(null)
  }

  override fun initWidgets() {
    this.addWidget(WString(I18n.format("entitybank.savebank.info"), 76, 103))
    this.addWidget(BUTTON_YES)
    this.addWidget(BUTTON_NO)
  }

  override fun getStyle(): IStyleUI = IStyleUI.INSTANCE

  override fun drawBackground(sign: Int) {
    this.drawWorldBackground(sign)
  }

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
  }
}
