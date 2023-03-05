package nl.roka.skipbo.skipboserver.lobby;

import nl.roka.skipbo.skipboserver.lobby.api.commands.HostNewLobby;
import nl.roka.skipbo.skipboserver.lobby.api.commands.JoinLobby;
import nl.roka.skipbo.skipboserver.lobby.api.commands.LeaveLobby;
import nl.roka.skipbo.skipboserver.lobby.api.events.LobbyClosed;
import nl.roka.skipbo.skipboserver.lobby.api.events.LobbyOpened;
import nl.roka.skipbo.skipboserver.lobby.api.events.LobbyRemoved;
import nl.roka.skipbo.skipboserver.lobby.api.events.NewLobyHosted;
import nl.roka.skipbo.skipboserver.lobby.api.events.PlayerJoinedLobby;
import nl.roka.skipbo.skipboserver.lobby.api.events.PlayerLeftLobby;
import nl.roka.skipbo.skipboserver.lobby.api.events.PlayerRejected;
import nl.roka.skipbo.skipboserver.lobby.api.model.LobbyId;
import nl.roka.skipbo.skipboserver.lobby.api.model.PlayerId;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class GameLobby {

  public static final int MAX_PLAYERS = 2;

  @AggregateIdentifier
  private LobbyId lobbyId;

  private List<PlayerId> players;

  public GameLobby() {
  }

  @CommandHandler
  protected GameLobby(HostNewLobby command, IdGenerator idGenerator) {
    LobbyId id = new LobbyId(idGenerator.generateId());
    apply(new NewLobyHosted(id));
    apply(new LobbyOpened(id));
  }

  @EventSourcingHandler
  void handle(NewLobyHosted event) {
    this.lobbyId = event.lobbyId();
    this.players = new ArrayList<>();
  }

  @CommandHandler
  void handle(JoinLobby command) {
    if (players.contains(command.playerId()))
      return;

    if (players.size() == MAX_PLAYERS) {
      apply(new PlayerRejected(this.lobbyId, command.playerId()));
      return;
    }

    apply(new PlayerJoinedLobby(this.lobbyId, command.playerId()));
    if (players.size() == MAX_PLAYERS) {
      apply(new LobbyClosed(this.lobbyId));
    }
  }

  @EventSourcingHandler
  void handle(PlayerJoinedLobby event) {
    this.players.add(event.playerId());
  }

  @CommandHandler
  void handle(LeaveLobby command) {
    if (!players.contains(command.playerId()))
      return;

    apply(new PlayerLeftLobby(this.lobbyId, command.playerId()));
    if (players.size() == MAX_PLAYERS - 1) {
      apply(new LobbyOpened(this.lobbyId));
    } else if (players.size() == 0) {
      apply(new LobbyRemoved(this.lobbyId));
    }
  }

  @EventSourcingHandler
  void handle(PlayerLeftLobby event) {
    this.players.remove(event.playerId());
  }

  @EventSourcingHandler
  void handle(LobbyRemoved event) {
    this.players = null;
  }
}
