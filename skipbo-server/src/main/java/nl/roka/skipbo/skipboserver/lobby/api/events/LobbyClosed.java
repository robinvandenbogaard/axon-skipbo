package nl.roka.skipbo.skipboserver.lobby.api.events;

import nl.roka.skipbo.skipboserver.lobby.api.model.LobbyId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record LobbyClosed(@TargetAggregateIdentifier LobbyId lobbyId) {
}
