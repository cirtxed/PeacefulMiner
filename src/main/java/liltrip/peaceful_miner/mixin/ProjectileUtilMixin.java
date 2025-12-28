package liltrip.peaceful_miner.mixin;

import liltrip.peaceful_miner.util.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Predicate;

@Mixin(ProjectileUtil.class)
public class ProjectileUtilMixin {
    @ModifyVariable(method = "raycast", at = @At("HEAD"), argsOnly = true)
    private static Predicate<Entity> modifyPredicate(Predicate<Entity> predicate, Entity entity) {
        if (entity instanceof PlayerEntity player && PlayerUtil.isHoldingPickaxe(player)) {
            float range = liltrip.peaceful_miner.Peaceful_miner.CONFIG.mineThroughRange;
            float squaredRange = range * range;
            return (target) -> {
                if (target instanceof PlayerEntity) {
                    return player.squaredDistanceTo(target) > squaredRange;
                }
                return predicate.test(target);
            };
        }
        return predicate;
    }
}
