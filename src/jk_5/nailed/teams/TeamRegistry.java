package jk_5.nailed.teams;

import com.google.common.collect.Maps;
import jk_5.nailed.Nailed;
import jk_5.nailed.config.helper.ConfigFile;
import jk_5.nailed.config.helper.ConfigTag;
import jk_5.nailed.players.Player;
import jk_5.nailed.util.EnumColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ScorePlayerTeam;
import net.minecraft.src.Scoreboard;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * No description given
 *
 * @author jk-5
 */
public class TeamRegistry {

    private Map<String, Team> teams = Maps.newHashMap();
    private Map<String, Team> playerTeamMap = Maps.newHashMap();

    public static ScorePlayerTeam spectatorTeam;

    public void setupTeams() { //TODO: fix!
        /*ConfigFile conf = Nailed.mapLoader.getMap(0).getConfig();
        List<ConfigTag> teams = conf.getTag("teams").getSortedTagList();
        if (teams == null) return;
        Scoreboard s = this.getScoreboardFromWorldServer();
        for (ConfigTag tag : teams) {
            EnumColor color = EnumColor.valueOf(tag.getTag("color").getValue("white").toUpperCase());
            String name = tag.getTag("name").getValue("");
            Team team = new Team(name, tag.name(), color);
            this.teams.put(tag.name(), team);
            ScorePlayerTeam scoreboardTeam = s.func_96527_f(tag.name());                            //teamid
            scoreboardTeam.func_96664_a(name);                                                      //teamname
            scoreboardTeam.func_96660_a(tag.getTag("frienlyfire").getBooleanValue(true));           //friendlyfire
            scoreboardTeam.func_98300_b(tag.getTag("friendlyinvisibles").getBooleanValue(true));    //friendlyinvisibles
            scoreboardTeam.func_96666_b(color.toString());                                          //teamprefix
            scoreboardTeam.func_96662_c(EnumColor.RESET.toString());                                //teamsuffix
            team.scoreboardTeam = scoreboardTeam;
        }*/
    }

    private void createSpectatorTeam(){
        Scoreboard s = this.getScoreboardFromWorldServer();
        spectatorTeam = s.func_96527_f("spectator");
        spectatorTeam.func_96664_a("Spectator");                   //teamname
        spectatorTeam.func_96660_a(false);                         //friendlyfire
        spectatorTeam.func_98300_b(true);                          //friendlyinvisibles
        spectatorTeam.func_96666_b(EnumColor.AQUA.toString());     //teamprefix
        spectatorTeam.func_96662_c(EnumColor.RESET.toString());    //teamsuffix
    }

    private Scoreboard getScoreboardFromWorldServer() {
        return MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();
    }

    public Team getTeamFromPlayer(String username) {
        if (!this.playerTeamMap.containsKey(username)) return Team.UNKNOWN;
        else return this.playerTeamMap.get(username);
    }

    public void setPlayerTeam(Player p, Team t) {
        Team current = this.playerTeamMap.get(p.getUsername());
        if (current != null) {
            this.playerTeamMap.remove(p.getUsername());
            this.getScoreboardFromWorldServer().func_96524_g(p.getUsername()); //leave
        }
        if (t.scoreboardTeam != null)
            this.getScoreboardFromWorldServer().func_96521_a(p.getUsername(), t.scoreboardTeam);
        this.playerTeamMap.put(p.getUsername(), t);
    }

    public Team getTeam(String teamId) {
        return this.teams.get(teamId);
    }

    public Set<String> getTeamNames() {
        return this.teams.keySet();
    }
}
