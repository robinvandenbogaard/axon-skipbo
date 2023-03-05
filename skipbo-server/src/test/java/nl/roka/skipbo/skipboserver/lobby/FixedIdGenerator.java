package nl.roka.skipbo.skipboserver.lobby;

import org.springframework.util.IdGenerator;

import javax.annotation.Nonnull;
import java.util.Queue;
import java.util.UUID;

public class FixedIdGenerator implements IdGenerator {

  private final Queue<UUID> queue;

  public FixedIdGenerator(Queue<UUID> queue) {
    this.queue = queue;
  }

  @Override
  @Nonnull
  public UUID generateId() {
    return queue.remove();
  }
}
