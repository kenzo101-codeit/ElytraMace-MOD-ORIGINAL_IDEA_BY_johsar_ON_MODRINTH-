package net.minecraft.server.jsonrpc.internalapi;

import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.jsonrpc.JsonRpcLogger;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.jsonrpc.methods.GameRulesService;
import net.minecraft.server.jsonrpc.methods.InvalidParameterJsonRpcException;
import net.minecraft.world.flag.FeatureFlagSet;

public class MinecraftGameRuleServiceImpl implements MinecraftGameRuleService {
    private final DedicatedServer server;
    private final JsonRpcLogger jsonrpcLogger;

    public MinecraftGameRuleServiceImpl(DedicatedServer p_422375_, JsonRpcLogger p_428405_) {
        this.server = p_422375_;
        this.jsonrpcLogger = p_428405_;
    }

    @Override
    public GameRulesService.TypedRule updateGameRule(GameRulesService.UntypedRule p_426643_, ClientInfo p_425938_) {
        net.minecraft.world.level.GameRules.Value<?> value = this.getRuleValue(p_426643_.key());
        String s = value.serialize();
        if (value instanceof net.minecraft.world.level.GameRules.BooleanValue gamerules$booleanvalue) {
            gamerules$booleanvalue.set(Boolean.parseBoolean(p_426643_.value()), this.server);
        } else {
            if (!(value instanceof net.minecraft.world.level.GameRules.IntegerValue gamerules$integervalue)) {
                throw new InvalidParameterJsonRpcException("Unknown rule type for key: " + p_426643_.key());
            }

            gamerules$integervalue.set(Integer.parseInt(p_426643_.value()), this.server);
        }

        GameRulesService.TypedRule gamerulesservice$typedrule = this.getTypedRule(p_426643_.key(), value);
        this.jsonrpcLogger
            .log(p_425938_, "Game rule '{}' updated from '{}' to '{}'", gamerulesservice$typedrule.key(), s, gamerulesservice$typedrule.value());
        this.server.onGameRuleChanged(p_426643_.key(), value);
        return gamerulesservice$typedrule;
    }

    @Override
    public <T extends net.minecraft.world.level.GameRules.Value<T>> T getRule(net.minecraft.world.level.GameRules.Key<T> p_422498_) {
        return this.server.getGameRules().getRule(p_422498_);
    }

    @Override
    public GameRulesService.TypedRule getTypedRule(String p_423825_, net.minecraft.world.level.GameRules.Value<?> p_427665_) {
        return switch (p_427665_) {
            case net.minecraft.world.level.GameRules.BooleanValue gamerules$booleanvalue -> new GameRulesService.TypedRule(
                p_423825_, String.valueOf(gamerules$booleanvalue.get()), GameRulesService.RuleType.BOOL
            );
            case net.minecraft.world.level.GameRules.IntegerValue gamerules$integervalue -> new GameRulesService.TypedRule(
                p_423825_, String.valueOf(gamerules$integervalue.get()), GameRulesService.RuleType.INT
            );
            default -> throw new InvalidParameterJsonRpcException("Unknown rule type");
        };
    }

    @Override
    public Stream<Entry<net.minecraft.world.level.GameRules.Key<?>, net.minecraft.world.level.GameRules.Type<?>>> getAvailableGameRules() {
        FeatureFlagSet featureflagset = this.server.getWorldData().getLevelSettings().getDataConfiguration().enabledFeatures();
        return net.minecraft.world.level.GameRules.availableRules(featureflagset);
    }

    private Optional<net.minecraft.world.level.GameRules.Key<?>> getRuleKey(String p_429307_) {
        Stream<Entry<net.minecraft.world.level.GameRules.Key<?>, net.minecraft.world.level.GameRules.Type<?>>> stream = this.getAvailableGameRules();
        return stream.filter(p_428975_ -> p_428975_.getKey().getId().equals(p_429307_)).findFirst().map(Entry::getKey);
    }

    private net.minecraft.world.level.GameRules.Value<?> getRuleValue(String p_430910_) {
        net.minecraft.world.level.GameRules.Key<?> key = this.getRuleKey(p_430910_)
            .orElseThrow(() -> new InvalidParameterJsonRpcException("Game rule '" + p_430910_ + "' does not exist"));
        return this.server.getGameRules().getRule((net.minecraft.world.level.GameRules.Key)key);
    }
}