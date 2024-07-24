package dev.syoritohatsuki.learnenglish.client.mixin;

import dev.syoritohatsuki.learnenglish.client.Translations;
import dev.syoritohatsuki.learnenglish.client.gui.widget.CyclinglessButtonWidget;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(LanguageOptionsScreen.class)
public abstract class LanguageOptionsScreenMixin extends GameOptionsScreen {

    @Unique
    private static final String URL = "https://www.duolingo.com";

    @Shadow
    private LanguageOptionsScreen.LanguageSelectionListWidget languageSelectionList;

    @Shadow
    abstract void onDone();

    public LanguageOptionsScreenMixin(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @ModifyArg(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/option/LanguageOptionsScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;",
                    ordinal = 1
            )
    )
    private Element changeDoneButtonWidget(Element element) {
        return CyclinglessButtonWidget.onOffBuilder(Text.literal("Open Duolingo"), ScreenTexts.DONE)
                .omitKeyText()
                .initially(false)
                .dimensions(this.width / 2 - 155 + 160, this.height - 38, 150, 20)
                .build(null, (button, value) -> onDone());
    }

    @Inject(method = "onDone", at = @At(value = "HEAD"), cancellable = true)
    private void openDuolingo(CallbackInfo ci) {
        if (FabricLoader.getInstance().isModLoaded("languagereload")) return;

        LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry languageEntry = languageSelectionList.getSelectedOrNull();
        if (languageEntry != null && Translations.translations.containsKey(languageEntry.languageCode)) {
            if (client != null) client.setScreen(new ConfirmLinkScreen(confirmed -> {
                if (confirmed) Util.getOperatingSystem().open(URL);
                client.setScreen(parent);
            }, URL, false));
            ci.cancel();
        }

    }
}