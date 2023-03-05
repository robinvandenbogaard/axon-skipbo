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
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.IdGenerator;

import java.util.ArrayDeque;
import java.util.List;

import static nl.roka.skipbo.skipboserver.lobby.LobbyFixture.LOBBY1;
import static nl.roka.skipbo.skipboserver.lobby.PlayerFixture.PLAYER1;
import static nl.roka.skipbo.skipboserver.lobby.PlayerFixture.PLAYER2;
import static nl.roka.skipbo.skipboserver.lobby.PlayerFixture.PLAYER3;

class GameLobbyTest {

  private FixtureConfiguration<GameLobby> fixture;

  @BeforeEach
  public void setUp() {
    IdGenerator generator = new FixedIdGenerator(new ArrayDeque<>(List.of(LOBBY1.uuid())));

    fixture = new AggregateTestFixture<>(GameLobby.class);
    fixture.registerInjectableResource(generator);
  }

  @Test
  public void testHostNewLobbyCommand() {
    fixture.given()
           .when(new HostNewLobby())
           .expectSuccessfulHandlerExecution()
           .expectEvents(new NewLobyHosted(LOBBY1), new LobbyOpened(LOBBY1));
  }

  @Test
  public void testJoinLobbyCommandFirstPlayer() {
    fixture.given(new NewLobyHosted(LOBBY1))
           .when(new JoinLobby(LOBBY1, PLAYER1))
           .expectSuccessfulHandlerExecution()
           .expectEvents(new PlayerJoinedLobby(LOBBY1, PLAYER1));
  }

  @Test
  public void testJoinLobbyCommandSecondPlayer() {
    fixture.given(new NewLobyHosted(LOBBY1))
           .andGiven(new PlayerJoinedLobby(LOBBY1, PLAYER1))
           .when(new JoinLobby(LOBBY1, PLAYER2))
           .expectSuccessfulHandlerExecution()
           .expectEvents(new PlayerJoinedLobby(LOBBY1, PLAYER2), new LobbyClosed(LOBBY1));
  }

  @Test
  public void testJoinFullLobbyCommand() {
    fixture.given(new NewLobyHosted(LOBBY1))
           .andGiven(new PlayerJoinedLobby(LOBBY1, PLAYER1))
           .andGiven(new PlayerJoinedLobby(LOBBY1, PLAYER2))
           .when(new JoinLobby(LOBBY1, PLAYER3))
           .expectSuccessfulHandlerExecution()
           .expectEvents(new PlayerRejected(LOBBY1, PLAYER3));
  }

  @Test
  public void testLeaveLobbyCommand() {
    fixture.given(new NewLobyHosted(LOBBY1))
           .andGiven(new PlayerJoinedLobby(LOBBY1, PLAYER1))
           .andGiven(new PlayerJoinedLobby(LOBBY1, PLAYER2))
           .when(new LeaveLobby(LOBBY1, PLAYER2))
           .expectSuccessfulHandlerExecution()
           .expectEvents(new PlayerLeftLobby(LOBBY1, PLAYER2), new LobbyOpened(LOBBY1));
  }

  @Test
  public void testLeaveLobbyYouAreInCommand() {
    fixture.given(new NewLobyHosted(LOBBY1))
           .andGiven(new PlayerJoinedLobby(LOBBY1, PLAYER1))
           .andGiven(new PlayerJoinedLobby(LOBBY1, PLAYER2))
           .when(new LeaveLobby(LOBBY1, PLAYER3))
           .expectSuccessfulHandlerExecution()
           .expectNoEvents();
  }

  @Test
  public void testCanJoinLobbyOnlyOnce() {
    fixture.given(new NewLobyHosted(LOBBY1))
           .andGiven(new PlayerJoinedLobby(LOBBY1, PLAYER1))
           .when(new JoinLobby(LOBBY1, PLAYER1))
           .expectSuccessfulHandlerExecution()
           .expectNoEvents();
  }

  @Test
  public void testLobbyGetsRemovedIfLastPlayerLeaves() {
    fixture.given(new NewLobyHosted(LOBBY1))
           .andGiven(new PlayerJoinedLobby(LOBBY1, PLAYER1))
           .andGiven(new PlayerJoinedLobby(LOBBY1, PLAYER2))
           .andGiven(new PlayerLeftLobby(LOBBY1, PLAYER1))
           .when(new LeaveLobby(LOBBY1, PLAYER2))
           .expectSuccessfulHandlerExecution()
           .expectEvents(new PlayerLeftLobby(LOBBY1, PLAYER2), new LobbyRemoved(LOBBY1));
  }

}