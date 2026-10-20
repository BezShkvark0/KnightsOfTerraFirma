package net.witchkings.knightsofterrafirma.mixin;

import com.magistuarmory.item.armor.ArmorType;
import net.minecraft.world.item.ArmorItem;
import net.witchkings.knightsofterrafirma.item.ArmorModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ArmorType.class, priority = 10000)
public class ArmorTypeMixin {
    @Inject(method = "getToughness", at = @At("HEAD"), cancellable = true)
    private void kotfToughness(CallbackInfoReturnable<Float> cir) {
        var stats = ArmorModifier.OVERRIDES.get((ArmorType) (Object) this);
        if (stats != null) {
            cir.setReturnValue(stats.toughness());
        }
    }

    @Inject(method = "getKnockbackResistance", at = @At("HEAD"), cancellable = true)
    private void kotfKnockback(CallbackInfoReturnable<Float> cir) {
        var stats = ArmorModifier.OVERRIDES.get((ArmorType) (Object) this);
        if (stats != null) {
            cir.setReturnValue(stats.knockbackResistance());
        }
    }

    @Inject(method = "getDurabilityForType", at = @At("HEAD"), cancellable = true)
    private void kotfDurability(ArmorItem.Type type, CallbackInfoReturnable<Integer> cir) {
        var stats = ArmorModifier.OVERRIDES.get((ArmorType) (Object) this);
        if (stats != null) {
            cir.setReturnValue(stats.durability(type));
        }
    }

    @Inject(method = "getDefenseForType", at = @At("HEAD"), cancellable = true)
    private void kotfDefense(ArmorItem.Type type, CallbackInfoReturnable<Integer> cir) {
        var stats = ArmorModifier.OVERRIDES.get((ArmorType) (Object) this);
        if (stats != null) {
            cir.setReturnValue(stats.defense(type));
        }
    }
}
