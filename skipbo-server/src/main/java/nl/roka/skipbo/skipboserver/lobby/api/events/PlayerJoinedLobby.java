package nl.roka.skipbo.skipboserver.lobby.api.events;

import nl.roka.skipbo.skipboserver.lobby.api.model.LobbyId;
import nl.roka.skipbo.skipboserver.lobby.api.model.PlayerId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record PlayerJoinedLobby(@TargetAggregateIdentifier LobbyId lobbyId, PlayerId playerId) {
}
