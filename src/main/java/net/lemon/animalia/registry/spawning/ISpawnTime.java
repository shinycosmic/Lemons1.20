package net.lemon.animalia.registry.spawning;

public interface ISpawnTime {
    default SpawnTime spawnTime() {
        return SpawnTime.ANY;
    }
}
