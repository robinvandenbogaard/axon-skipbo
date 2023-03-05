package nl.roka.skipbo.skipboserver.lobby;

import nl.roka.skipbo.skipboserver.lobby.api.model.PlayerId;

import java.util.UUID;

public class PlayerFixture {
  public static final PlayerId PLAYER1 = new PlayerId(UUID.randomUUID());
  public static final PlayerId PLAYER2 = new PlayerId(UUID.randomUUID());
  public static final PlayerId PLAYER3 = new PlayerId(UUID.randomUUID());
}
