package dev.syoritohatsuki.learnenglish.client.mixin.integration.languagereload;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.syoritohatsuki.learnenglish.client.Translations;
import jerozgen.languagereload.gui.LanguageEntry;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.LanguageDefinition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LanguageEntry.class)
public abstract class LanguageEntryMixin {
    @Shadow @Final private String code;

    @Shadow @Final private LanguageDefinition language;

    @Redirect(method = "render", at = @At(value = "FIELD", target = "Ljerozgen/languagereload/gui/LanguageEntry;language:Lnet/minecraft/client/resource/language/LanguageDefinition;"))
    private LanguageDefinition changeNonEnglishLanguagesLabels(LanguageEntry instance) {
        var translate = Translations.translations.get(code);
        return translate != null ? new LanguageDefinition(translate, translate, false) : language;
    }

    @WrapWithCondition(
            method = "renderButtons",
            at = @At(value = "INVOKE", target = "Ljerozgen/languagereload/gui/LanguageEntry$ButtonRenderer;render(Lnet/minecraft/client/gui/widget/ButtonWidget;II)V", ordinal = 3)
    )
    private boolean onlyRenderIfAllowed(@Coerce Object instance, ButtonWidget buttonWidget, int i, int j) {
        return Translations.translations.get(code) == null;
    }
}
