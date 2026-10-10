package club.someoneice.manametal.entitybank

import alexsocol.asjlib.mc
import alexsocol.patcher.KotlinAdapter
import alexsocol.patcher.KotlinProxy
import club.someoneice.manametal.entitybank.client.gui.GuiSaveToBank
import club.someoneice.manametal.entitybank.client.gui.GuiWaiting
import club.someoneice.manametal.entitybank.network.NetworkHandler
import com.github.artbits.quickio.api.JKV
import com.github.artbits.quickio.core.Config
import com.github.artbits.quickio.core.QuickIO
import com.google.common.base.Supplier
import com.google.common.base.Suppliers
import cpw.mods.fml.client.registry.ClientRegistry
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.InputEvent
import cpw.mods.fml.common.network.FMLEventChannel
import cpw.mods.fml.common.network.NetworkRegistry
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import jdk.jfr.internal.SecuritySupport.registerEvent
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.common.MinecraftForge
import org.lwjgl.input.Keyboard
import project.studio.manametalmod.items.ItemSoulBottle
import kotlin.jvm.java


@Mod(modid = EntityBank.MODID, useMetadata = true, modLanguage = "kotlin", modLanguageAdapter = KotlinAdapter.className)
class EntityBank {
  companion object {
    const val MODID = "entitybank"
    const val NAME = "The Entity Bank"
    val DB_PLAYER_DATA_CONFIG: Config = Config.of { it.name("player_data").path("entitybank") }
    val KEYBIND = KeyBinding("entitybank.keybind.name", Keyboard.KEY_Y, "key.categories.misc")
    val SOULBOTTLE: Supplier<ItemSoulBottle> = Suppliers.memoize {
      GameRegistry.findItem("manametalmod", "TESTSyringes") as ItemSoulBottle
    }

    val NETWORK: FMLEventChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(MODID)

    @Mod.Instance
    lateinit var INSTANCE: EntityBank

    fun createDB(): JKV = QuickIO.kv(DB_PLAYER_DATA_CONFIG)

    fun registerEvent(obj: Any) {
      MinecraftForge.EVENT_BUS.register(obj)
      FMLCommonHandler.instance().bus().register(obj)

    }

    @KotlinProxy(
      clientSide = "club.someoneice.manametal.entitybank.ProxyClient",
      serverSide = "club.someoneice.manametal.entitybank.ProxyServer"
    ) lateinit var proxy: IProxy
  }

  @Mod.EventHandler
  fun preInit(e: FMLPreInitializationEvent) {
    NETWORK.register(NetworkHandler)
    registerEvent(ThreadDataHandler)

    proxy.init()
  }
}
