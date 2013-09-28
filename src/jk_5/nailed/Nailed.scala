package jk_5.nailed

import com.google.common.eventbus.EventBus
import jk_5.nailed.config.helper.ConfigFile
import java.io.File
import jk_5.nailed.map.MapLoader
import jk_5.nailed.map.stats.StatManager
import jk_5.nailed.irc.IrcConnector
import jk_5.nailed.teamspeak3.TeamspeakManager
import net.minecraft.src.{CommandHandler, DedicatedServer}
import jk_5.nailed.groups.{GroupRegistry, GroupAdmin, GroupPlayer}
import jk_5.nailed.ipc.IPCClient
import jk_5.nailed.command._

/**
 * No description given
 *
 * @author jk-5
 */
object Nailed {
  final val eventBus = new EventBus
  final val config = new ConfigFile(new File("nailed.cfg")).setComment("Nailed main config file")
  final val mapLoader = new MapLoader
  final val statManager = new StatManager
  final val irc = new IrcConnector
  final val teamspeak = new TeamspeakManager

  var server: DedicatedServer = _

  def init(server: DedicatedServer){
    this.server = server

    this.eventBus.register(NailedEventListener)

    this.mapLoader.loadMaps()

    GroupRegistry.registerGroup("player", new GroupPlayer())
    GroupRegistry.registerGroup("admin", new GroupAdmin())
    GroupRegistry.setDefaultGroup("player")

    this.mapLoader.setupLobby()

    this.irc.connect()
    IPCClient.start()

    //this.teamspeak.setEnabled(false); //Disable it, it's broke like a joke
    this.teamspeak.connect()
  }

  def onWorldReady() {
    val map1 = this.mapLoader.createWorld(mapLoader.getMappack("normalLobby"))
    val map2 = this.mapLoader.createWorld(mapLoader.getMappack("raceforwool"))

    this.mapLoader.setupMapSettings()
  }

  def registerCommands(handler: CommandHandler){
    handler.registerCommand(CommandCB)
    handler.registerCommand(new CommandGroup())
    handler.registerCommand(new CommandTeam())
    handler.registerCommand(CommandWorld)
    handler.registerCommand(new CommandSpectator())
    handler.registerCommand(new CommandStartGame())
    handler.registerCommand(CommandBroadcastChat)
    handler.registerCommand(new CommandTeamleader())
    handler.registerCommand(new CommandReady())
    handler.registerCommand(new CommandServerMode())
    handler.registerCommand(CommandReconnectIPC)
    if (teamspeak.isEnabled) handler.registerCommand(new CommandTeamspeak())
  }
}
