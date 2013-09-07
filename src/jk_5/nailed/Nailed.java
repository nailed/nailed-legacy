package jk_5.nailed;

import com.google.common.eventbus.EventBus;
import jk_5.nailed.command.*;
import jk_5.nailed.config.helper.ConfigFile;
import jk_5.nailed.groups.GroupAdmin;
import jk_5.nailed.groups.GroupPlayer;
import jk_5.nailed.groups.GroupRegistry;
import jk_5.nailed.irc.IrcConnector;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.stats.StatManager;
import jk_5.nailed.network.IPCClient;
import jk_5.nailed.players.PlayerRegistry;
import jk_5.nailed.teams.TeamRegistry;
import jk_5.nailed.teamspeak3.TeamspeakManager;
import jk_5.nailed.util.EnumColor;
import net.minecraft.src.CommandHandler;
import net.minecraft.src.DedicatedServer;

import java.io.File;

/**
 * Main nailed class. Controls the initialization and contains all helpers that are needed for the server
 *
 * @author jk-5
 */
public class Nailed {
    public static final EventBus eventBus = new EventBus();
    public static final TeamRegistry teamRegistry = new TeamRegistry();
    public static final PlayerRegistry playerRegistry = new PlayerRegistry();
    public static final GroupRegistry groupRegistry = new GroupRegistry();
    public static final ConfigFile config = new ConfigFile(new File("nailed.cfg")).setComment("Nailed main config file");
    public static final MapLoader mapLoader = new MapLoader();
    public static final StatManager statManager = new StatManager();
    public static final IrcConnector irc = new IrcConnector();
    public static final IPCClient ipc = new IPCClient();
    public static final TeamspeakManager teamspeak = new TeamspeakManager();
    public static DedicatedServer server;

    public static void init(DedicatedServer server) {
        Nailed.server = server;

        server.setMOTD(EnumColor.GREEN + "[Nail]" + EnumColor.RESET + " Ohai!");

        eventBus.register(new NailedEventListener());
        eventBus.register(playerRegistry);

        mapLoader.loadMaps();

        groupRegistry.registerGroup("player", new GroupPlayer());
        groupRegistry.registerGroup("admin", new GroupAdmin());
        groupRegistry.setDefaultGroup("player");

        mapLoader.setupLobby();

        irc.connect();
        ipc.start();

        teamspeak.setEnabled(false); //Disable it, it's broke like a joke
        teamspeak.connect();
    }

    public static void onWorldReady() {
        teamRegistry.setupTeams();

        Map map1 = mapLoader.createWorld(mapLoader.getMappack("nail"));
        Map map2 = mapLoader.createWorld(mapLoader.getMappack("nail"));

        //WorldServer world1 = Nailed.multiworldManager.createNewMapDimension(1);
        //Nailed.multiworldManager.prepareSpawnForWorld(1);

        mapLoader.setupMapSettings();
    }

    public static void registerCommands(CommandHandler handler) {
        handler.registerCommand(new CommandCB());
        handler.registerCommand(new CommandGroup());
        handler.registerCommand(new CommandTeam());
        handler.registerCommand(new CommandNewWorld());
        handler.registerCommand(new CommandSpectator());
        handler.registerCommand(new CommandStartGame());
        handler.registerCommand(new CommandBroadcastChat());
        if(teamspeak.isEnabled()) handler.registerCommand(new CommandTeamspeak());
    }
}
