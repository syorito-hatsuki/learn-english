package dev.syoritohatsuki.learnenglish.client.gui.widget;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class CyclinglessButtonWidget<T> extends PressableWidget {
    public static final BooleanSupplier HAS_ALT_DOWN = Screen::hasAltDown;
    private static final List<Boolean> BOOLEAN_VALUES = ImmutableList.of(Boolean.TRUE, Boolean.FALSE);
    private final Text optionText;
    private int index;
    private T value;
    private final CyclinglessButtonWidget.Values<T> values;
    private final Function<T, Text> valueToText;
    private final CyclinglessButtonWidget.UpdateCallback<T> callback;
    private final boolean optionTextOmitted;

    public CyclinglessButtonWidget(int x, int y, int width, int height, Text message, Text optionText, int index, T value, CyclinglessButtonWidget.Values<T> values, Function<T, Text> valueToText, CyclinglessButtonWidget.UpdateCallback<T> callback, boolean optionTextOmitted) {
        super(x, y, width, height, message);
        this.optionText = optionText;
        this.index = index;
        this.value = value;
        this.values = values;
        this.valueToText = valueToText;
        this.callback = callback;
        this.optionTextOmitted = optionTextOmitted;
    }

    @Override
    public void onPress() {
        List<T> list = this.values.getCurrent();
        T object = list.get(this.index);
        this.internalSetValue(object);
        this.callback.onValueChange(this, object);
    }

    private T getOffsetValue() {
        List<T> list = this.values.getCurrent();
        return list.get(MathHelper.floorMod(this.index + 1, list.size()));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return true;
    }

    public void setValue(T value) {
        List<T> list = this.values.getCurrent();
        int i = list.indexOf(value);
        if (i != -1) {
            this.index = i;
        }

        this.internalSetValue(value);
    }

    private void internalSetValue(T value) {
        Text text = this.composeText(value);
        this.setMessage(text);
        this.value = value;
    }

    private Text composeText(T value) {
        return this.optionTextOmitted ? this.valueToText.apply(value) : this.composeGenericOptionText(value);
    }

    private MutableText composeGenericOptionText(T value) {
        return ScreenTexts.composeGenericOptionText(this.optionText, this.valueToText.apply(value));
    }

    @Override
    protected MutableText getNarrationMessage() {
        return getGenericNarrationMessage();
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.getNarrationMessage());
        if (this.active) {
            T object = this.getOffsetValue();
            Text text = this.composeText(object);
            if (this.isFocused()) {
                builder.put(NarrationPart.USAGE, Text.translatable("narration.cycle_button.usage.focused", text));
            } else {
                builder.put(NarrationPart.USAGE, Text.translatable("narration.cycle_button.usage.hovered", text));
            }
        }
    }

    public MutableText getGenericNarrationMessage() {
        return getNarrationMessage(this.optionTextOmitted ? this.composeGenericOptionText(this.value) : this.getMessage());
    }

    public static CyclinglessButtonWidget.Builder<Boolean> onOffBuilder(Text on, Text off) {
        return new CyclinglessButtonWidget.Builder<Boolean>(value -> value ? on : off).values(BOOLEAN_VALUES);
    }

    @Environment(EnvType.CLIENT)
    public static class Builder<T> {
        private int initialIndex;
        @Nullable
        private T value;
        private final Function<T, Text> valueToText;
        private CyclinglessButtonWidget.Values<T> values = CyclinglessButtonWidget.Values.of(ImmutableList.<T>of());
        private boolean optionTextOmitted;
        private int x;
        private int y;
        private int width = 150;
        private int height = 20;

        public Builder(Function<T, Text> valueToText) {
            this.valueToText = valueToText;
        }

        public CyclinglessButtonWidget.Builder<T> position(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public CyclinglessButtonWidget.Builder<T> size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public CyclinglessButtonWidget.Builder<T> dimensions(int x, int y, int width, int height) {
            return position(x, y).size(width, height);
        }

        public CyclinglessButtonWidget.Builder<T> values(Collection<T> values) {
            return this.values(CyclinglessButtonWidget.Values.of(values));
        }

        @SafeVarargs
        public final CyclinglessButtonWidget.Builder<T> values(T... values) {
            return this.values(ImmutableList.copyOf(values));
        }

        public CyclinglessButtonWidget.Builder<T> values(List<T> defaults, List<T> alternatives) {
            return this.values(CyclinglessButtonWidget.Values.of(CyclinglessButtonWidget.HAS_ALT_DOWN, defaults, alternatives));
        }

        public CyclinglessButtonWidget.Builder<T> values(BooleanSupplier alternativeToggle, List<T> defaults, List<T> alternatives) {
            return this.values(CyclinglessButtonWidget.Values.of(alternativeToggle, defaults, alternatives));
        }

        public CyclinglessButtonWidget.Builder<T> values(CyclinglessButtonWidget.Values<T> values) {
            this.values = values;
            return this;
        }

        public CyclinglessButtonWidget.Builder<T> initially(T value) {
            this.value = value;
            int i = this.values.getDefaults().indexOf(value);
            if (i != -1) {
                this.initialIndex = i;
            }

            return this;
        }

        public CyclinglessButtonWidget.Builder<T> omitKeyText() {
            this.optionTextOmitted = true;
            return this;
        }

        public CyclinglessButtonWidget<T> build(Text optionText, CyclinglessButtonWidget.UpdateCallback<T> callback) {
            return this.build(x, y, width, height, optionText, callback);
        }

        public CyclinglessButtonWidget<T> build(int x, int y, int width, int height, Text optionText, CyclinglessButtonWidget.UpdateCallback<T> callback) {
            List<T> list = this.values.getDefaults();
            if (list.isEmpty()) {
                throw new IllegalStateException("No values for cycle button");
            } else {
                T object = this.value != null ? this.value : list.get(this.initialIndex);
                Text text = this.valueToText.apply(object);
                Text text2 = this.optionTextOmitted ? text : ScreenTexts.composeGenericOptionText(optionText, text);
                return new CyclinglessButtonWidget<>(x, y, width, height, text2, optionText, this.initialIndex, object, this.values, this.valueToText, callback, this.optionTextOmitted);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public interface UpdateCallback<T> {
        void onValueChange(CyclinglessButtonWidget<T> button, T value);
    }

    @Environment(EnvType.CLIENT)
    public interface Values<T> {
        List<T> getCurrent();

        List<T> getDefaults();

        static <T> CyclinglessButtonWidget.Values<T> of(Collection<T> values) {
            final List<T> list = ImmutableList.copyOf(values);
            return new CyclinglessButtonWidget.Values<>() {
                @Override
                public List<T> getCurrent() {
                    return list;
                }

                @Override
                public List<T> getDefaults() {
                    return list;
                }
            };
        }

        static <T> CyclinglessButtonWidget.Values<T> of(BooleanSupplier alternativeToggle, List<T> defaults, List<T> alternatives) {
            final List<T> list = ImmutableList.copyOf(defaults);
            final List<T> list2 = ImmutableList.copyOf(alternatives);
            return new CyclinglessButtonWidget.Values<>() {
                @Override
                public List<T> getCurrent() {
                    return alternativeToggle.getAsBoolean() ? list2 : list;
                }

                @Override
                public List<T> getDefaults() {
                    return list;
                }
            };
        }
    }
}
