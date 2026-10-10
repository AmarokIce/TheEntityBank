package club.someoneice.manametal.entitybank

import alexsocol.asjlib.mc
import club.someoneice.manametal.entitybank.client.gui.GuiSaveToBank
import club.someoneice.manametal.entitybank.client.gui.GuiWaiting
import cpw.mods.fml.client.registry.ClientRegistry
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.InputEvent
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import project.studio.manametalmod.items.ItemSoulBottle

class ProxyClient: IProxy {
  override fun init() {
    EntityBank.registerEvent(this)

    ClientRegistry.registerKeyBinding(EntityBank.KEYBIND)
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  fun keyInputEvent(event: InputEvent.KeyInputEvent) {
    if (!EntityBank.KEYBIND.isPressed) {
      return
    }

    if (mc.thePlayer.heldItem.item is ItemSoulBottle) {
      mc.displayGuiScreen(GuiSaveToBank)
      return
    }

    mc.displayGuiScreen(GuiWaiting())
  }
}
