package nl.roka.skipbo.skipboserver.lobby.api.commands;

import nl.roka.skipbo.skipboserver.lobby.api.model.LobbyId;
import nl.roka.skipbo.skipboserver.lobby.api.model.PlayerId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record JoinLobby(@TargetAggregateIdentifier LobbyId lobbyId, PlayerId playerId) {
}
