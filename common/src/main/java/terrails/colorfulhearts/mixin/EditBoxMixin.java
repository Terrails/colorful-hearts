package terrails.colorfulhearts.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import net.minecraft.client.gui.components.EditBox;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import terrails.colorfulhearts.config.screen.widgets.HeartColorEditBox;

@Mixin(EditBox.class)
public class EditBoxMixin {
    @WrapMethod(method = "setValue")
    private void maybeSetValue(String value, Operation<Void> original) {
        if (!((Object) this instanceof HeartColorEditBox heartColorEditBox) || heartColorEditBox.filter.test(value)) {
            original.call(value);
        }
    }

    @WrapOperation(
            method = {"deleteCharsToPos", "insertText"},
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/EditBox;value:Ljava/lang/String;", opcode = Opcodes.PUTFIELD)
    )
    private void maybeSetValue(EditBox instance, String value, Operation<Void> original, @Cancellable CallbackInfo ci) {
        if ((Object) this instanceof HeartColorEditBox heartColorEditBox && !heartColorEditBox.filter.test(value)) {
                ci.cancel();
                return;
        }
        original.call(instance, value);
    }
}
