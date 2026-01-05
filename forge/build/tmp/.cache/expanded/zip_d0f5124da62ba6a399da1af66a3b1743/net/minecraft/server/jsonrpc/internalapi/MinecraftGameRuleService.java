package net.minecraft.server.jsonrpc.internalapi;

import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.jsonrpc.methods.GameRulesService;

public interface MinecraftGameRuleService {
    GameRulesService.TypedRule updateGameRule(GameRulesService.UntypedRule p_430898_, ClientInfo p_429774_);

    <T extends net.minecraft.world.level.GameRules.Value<T>> T getRule(net.minecraft.world.level.GameRules.Key<T> p_423379_);

    GameRulesService.TypedRule getTypedRule(String p_423682_, net.minecraft.world.level.GameRules.Value<?> p_425188_);

    Stream<Entry<net.minecraft.world.level.GameRules.Key<?>, net.minecraft.world.level.GameRules.Type<?>>> getAvailableGameRules();
}