package dev.syoritohatsuki.learnenglish.client.mixin;

import dev.syoritohatsuki.learnenglish.client.LanguageOptionScreenAccessor;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LanguageOptionsScreen.LanguageSelectionListWidget.class)
public abstract class LanguageSelectionListWidgetMixin implements LanguageOptionScreenAccessor {

    @Final
    @Shadow(aliases = "field_18744")
    private LanguageOptionsScreen this$0;

    @Override
    public LanguageOptionsScreen learn_english$getLanguageOptionsScreen() {
        return this$0;
    }
}
