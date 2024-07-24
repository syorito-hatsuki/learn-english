package dev.syoritohatsuki.learnenglish.client.mixin;

import dev.syoritohatsuki.learnenglish.client.LanguageOptionScreenAccessor;
import dev.syoritohatsuki.learnenglish.client.Translations;
import dev.syoritohatsuki.learnenglish.client.gui.widget.CyclinglessButtonWidget;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry.class)
abstract class LanguageEntryMixin {
    @Final
    @Shadow
    public String languageCode;

    @Final
    @Shadow
    private Text languageDefinition;

    @Final
    @Shadow(aliases = "field_19100")
    private LanguageOptionsScreen.LanguageSelectionListWidget this$0;

    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/option/LanguageOptionsScreen$LanguageSelectionListWidget$LanguageEntry;languageDefinition:Lnet/minecraft/text/Text;"))
    private Text changeNonEnglishLanguagesLabels(LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry languageEntry) {
        var translate = Translations.translations.get(languageCode);
        return Text.of(translate != null ? translate : languageDefinition.getString());
    }

    @Inject(method = "onPressed", at = @At(value = "HEAD"))
    private void updateCyclicButton(CallbackInfo ci) {
        var translate = Translations.translations.get(languageCode);
        var layout = ((((LanguageOptionScreenAccessor) this$0).learn_english$getLanguageOptionsScreen())).children;
        for (Element element : layout) {
            if (element instanceof CyclinglessButtonWidget<?>) {
                ((CyclinglessButtonWidget<Boolean>) element).setValue(translate != null);
            }
        }
    }
}
