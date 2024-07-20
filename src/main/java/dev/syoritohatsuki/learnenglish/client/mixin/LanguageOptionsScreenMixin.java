package dev.syoritohatsuki.learnenglish.client.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(LanguageOptionsScreen.class)
public class LanguageOptionsScreenMixin extends GameOptionsScreen {
    public LanguageOptionsScreenMixin(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @Inject(method = "onDone", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resource/language/LanguageManager;setLanguage(Ljava/lang/String;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void openDuolingo(CallbackInfo ci) {
        Util.getOperatingSystem().open("https://www.duolingo.com");
        this.client.setScreen(this.parent);
        ci.cancel();
    }

    @Override
    public void addOptions() {
    }
}

@Mixin(LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry.class)
class LanguageEntryMixin {
    @Final
    @Shadow
    String languageCode;

    @Final
    @Shadow
    private Text languageDefinition;

    @Redirect(
            method = "render",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/gui/screen/option/LanguageOptionsScreen$LanguageSelectionListWidget$LanguageEntry;languageDefinition:Lnet/minecraft/text/Text;"
            )
    )
    private Text changeNonEnglishLanguagesLabels(LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry instance) {
        return languageCode.equals("en_us") ? languageDefinition : Text.literal("Learn English");
    }
}
