package club.someoneice.manametal.entitybank.client.gui

import club.someoneice.manametal.entitybank.client.StyleBackground
import club.someoneice.manametal.entitybank.client.WidgetEntitySlot
import club.someoneice.manametal.entitybank.network.NetworkHandler
import club.someoneice.manametal.entitybank.network.packet.C2SRemoveEntity
import club.someoneice.manametal.entitybank.network.packet.S2CEntities
import club.someoneice.wolftail.api.style.IStyleUI
import club.someoneice.wolftail.ui.core.WScreen
import club.someoneice.wolftail.ui.widget.WButton
import club.someoneice.wolftail.ui.widget.WScrollList
import club.someoneice.wolftail.util.UIPos
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.resources.I18n
import net.minecraft.entity.EntityLiving
import net.minecraft.nbt.NBTTagCompound
import project.studio.manametalmod.items.ItemSoulBottle
import java.util.*

class GuiBank(): WScreen(256, 256) {
  private val scroll = WScrollList(80, 0, 160, 256, StyleBackground)
  private var chose: WidgetEntitySlot? = null

  fun setChoseButton(chose: WidgetEntitySlot) {
    this.chose = chose
  }

  fun removeChild(node: WidgetEntitySlot) {
    this.scroll.removeChild(node)
  }

  override fun initWidgets() {
    this.addWidget(this.scroll)

    this.addWidget(WCtrlButton(I18n.format("entitybank.bank.gift"),
      UIPos(0, 150, 60, 20)) {
      this.mc.displayGuiScreen(GuiGift(this, this.chose!!))
      this.chose = null
    })
    this.addWidget(WCtrlButton(I18n.format("entitybank.bank.remove"),
      UIPos(0, 175, 60, 20)) {
      this.mc.displayGuiScreen(GuiRemoveEntity(this, this.chose!!))
      this.chose = null
    })
    this.addWidget(WCtrlButton(I18n.format("entitybank.bank.take"),
      UIPos(0, 200, 60, 20)) {
      NetworkHandler.sendPacket(C2SRemoveEntity(this.chose!!.entity.uniqueID, true))
      this.removeChild(this.chose!!)
      this.chose = null
    })

    var index = 0
    S2CEntities.PACK_RES
      .sortedWith(Comparator.comparingInt(S2CEntities::index))
      .flatMap(S2CEntities::entities)
      .mapNotNull(this::createEntityOrNull)
      .map { WidgetEntitySlot(index++, it, ::setChoseButton) }
      .forEach(this.scroll::addChild)
    S2CEntities.PACK_RES.clear()
  }

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    val x: Int = if (this.w == -1) 0 else (this.width - this.w) / 2
    val y: Int = if (this.h == -1) 0 else (this.height - this.h) / 2

    this.chose ?: return

    GuiInventory.func_147046_a(
      x + 25, y + 90, 30,
      x + 35 - mouseX.toFloat(), y + 25 - mouseY.toFloat(),
      this.chose!!.entity)
  }

  override fun drawBackground(sign: Int) {
    this.drawWorldBackground(sign)
  }

  override fun getStyle(): IStyleUI = IStyleUI.INSTANCE
  override fun doesGuiPauseGame(): Boolean = false

  private fun createEntityOrNull(nbt: NBTTagCompound): EntityLiving? {
    return ItemSoulBottle.createEntityFromNBT(nbt, mc.theWorld, 0, 0, 0) as? EntityLiving
  }

  inner class WCtrlButton(title: String, pos: UIPos, clicked: () -> Unit = {}): WButton(title, pos,
    StyleBackground, clicked) {
    override fun canBeClick(): Boolean {
      return Objects.nonNull(this@GuiBank.chose)
    }
  }
}
