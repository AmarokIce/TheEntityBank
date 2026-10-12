package club.someoneice.manametal.entitybank.network

import alexsocol.asjlib.mc
import club.someoneice.manametal.entitybank.client.gui.GuiSaveToBank
import club.someoneice.manametal.entitybank.client.gui.GuiWaiting
import club.someoneice.manametal.entitybank.core.EntityBank
import cpw.mods.fml.client.registry.ClientRegistry
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.InputEvent
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.client.settings.KeyBinding
import org.lwjgl.input.Keyboard
import project.studio.manametalmod.items.ItemSoulBottle
import java.util.*

class ProxyClient: IProxy {
  val KEYBIND = KeyBinding("entitybank.keybind.name", Keyboard.KEY_Y, "key.categories.misc")

  override fun init() {
    EntityBank.registerEvent(this)

    ClientRegistry.registerKeyBinding(KEYBIND)
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  fun keyInputEvent(event: InputEvent.KeyInputEvent) {
    if (!KEYBIND.isPressed) {
      return
    }

    if (Objects.nonNull(mc.thePlayer.heldItem) && mc.thePlayer.heldItem.item is ItemSoulBottle) {
      mc.displayGuiScreen(GuiSaveToBank)
      return
    }

    mc.displayGuiScreen(GuiWaiting())
  }
}
