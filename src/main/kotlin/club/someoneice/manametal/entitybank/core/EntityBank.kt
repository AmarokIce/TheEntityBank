package club.someoneice.manametal.entitybank.core

import alexsocol.patcher.KotlinAdapter
import alexsocol.patcher.KotlinProxy
import club.someoneice.manametal.entitybank.network.IProxy
import club.someoneice.manametal.entitybank.network.NetworkHandler
import com.google.common.base.Supplier
import com.google.common.base.Suppliers
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.network.FMLEventChannel
import cpw.mods.fml.common.network.NetworkRegistry
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraftforge.common.MinecraftForge
import project.studio.manametalmod.items.ItemSoulBottle


@Mod(modid = EntityBank.MODID, useMetadata = true, modLanguage = "kotlin", modLanguageAdapter = KotlinAdapter.className)
class EntityBank {
  companion object {
    const val MODID = "entitybank"
    const val NAME = "The Entity Bank"

    @Mod.Instance
    lateinit var INSTANCE: EntityBank

    @KotlinProxy(
      clientSide = "club.someoneice.manametal.entitybank.network.ProxyClient",
      serverSide = "club.someoneice.manametal.entitybank.network.ProxyServer"
    ) lateinit var proxy: IProxy

    val NETWORK: FMLEventChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(MODID)
    val SOULBOTTLE: Supplier<ItemSoulBottle> = Suppliers.memoize {
      GameRegistry.findItem("manametalmod", "TESTSyringes") as ItemSoulBottle
    }

    fun registerEvent(obj: Any) {
      MinecraftForge.EVENT_BUS.register(obj)
      FMLCommonHandler.instance().bus().register(obj)
    }
  }

  @Mod.EventHandler
  fun preInit(e: FMLPreInitializationEvent) {
    NETWORK.register(NetworkHandler)
    registerEvent(ThreadDataHandler)

    proxy.init()
  }
}
